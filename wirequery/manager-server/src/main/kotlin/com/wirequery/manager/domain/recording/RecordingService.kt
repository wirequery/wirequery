// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wirequery.manager.domain.FunctionalException.Companion.functionalError
import com.wirequery.manager.domain.recording.Recording.StatusEnum
import com.wirequery.manager.domain.recording.Recording.StatusEnum.*
import com.wirequery.manager.domain.recording.RecordingEvent.*
import com.wirequery.manager.domain.session.SessionService
import com.wirequery.manager.domain.session.SessionService.CreateSessionInput
import com.wirequery.manager.domain.template.TemplateService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.jvm.optionals.getOrNull

@Service
@Transactional
class RecordingService(
    private val recordingRepository: RecordingRepository,
    private val sessionService: SessionService,
    private val templateService: TemplateService,
    private val recordingSecretGenerator: RecordingSecretGenerator,
    private val clock: Clock,
    private val publisher: ApplicationEventPublisher,
) {
    private val objectMapper = jacksonObjectMapper()

    fun findById(id: Int): Recording? {
        return recordingRepository.findByIdOrNull(id)
            ?.let(::toDomainObject)
    }

    fun findByIds(ids: Iterable<Int>): List<Recording> {
        return recordingRepository.findByIds(ids)
            .map(::toDomainObject)
    }

    fun findBySessionIds(sessionIds: Iterable<Int>): List<Recording> {
        return recordingRepository.findBySessionIds(sessionIds)
            .map(::toDomainObject)
    }

    fun findByTemplateIds(templateIds: Iterable<Int>): List<Recording> {
        return recordingRepository.findByTemplateIds(templateIds)
            .map(::toDomainObject)
    }

    fun findAll(filterInput: RecordingFilterInput): List<Recording> {
        return recordingRepository.findBySessionIds(listOf(filterInput.sessionId))
            .map(::toDomainObject)
    }

    fun startRecording(input: StartRecordingInput): Recording {
        val template = templateService.findById(input.templateId)
        requireNotNull(template)
        if (!template.allowUserInitiation) {
            functionalError("Template with id ${input.templateId} does not allow user initiation.")
        }

        val session =
            sessionService.create(
                CreateSessionInput(
                    templateId = input.templateId,
                    variables =
                        input.args.map {
                            SessionService.CreateSessionInputFieldValue(it.key, it.value)
                        },
                    endDate = OffsetDateTime.now(clock).plusSeconds(RECORDING_TIMEOUT.toLong()),
                ),
                draft = true,
            )

        val recordingEntity =
            RecordingEntity(
                sessionId = session.id,
                templateId = input.templateId,
                args = objectMapper.writeValueAsString(input.args),
                secret = recordingSecretGenerator.generate(),
                lookBackSecs = LOOKBACK_SECS_PLACEHOLDER,
                timeoutSecs = RECORDING_TIMEOUT,
                recording = "",
                status = ACTIVE,
            )
        val recording = toDomainObject(recordingRepository.save(recordingEntity))
        publisher.publishEvent(RecordingsCreatedEvent(this, listOf(recording)))
        return recording
    }

    fun verifySecret(
        id: Int,
        secret: String,
    ): Boolean {
        return findById(id)?.secret == secret
    }

    fun cancelRecording(id: Int) {
        val recording = requireNotNull(recordingRepository.findById(id).getOrNull())

        recordingRepository.save(recording.copy(status = CANCELLED))

        sessionService.stopCapturing(recording.sessionId, false)

        sessionService.deleteById(recording.sessionId)
    }

    fun finishRecording(
        id: Int,
        recording: String,
    ) {
        val recordingEntity = requireNotNull(recordingRepository.findById(id).getOrNull())

        recordingRepository.save(
            recordingEntity.copy(
                status = FINISHED,
                recording = recording,
            ),
        )

        sessionService.stopCapturing(recordingEntity.sessionId, true)
    }

    fun update(
        id: Int,
        input: UpdateRecordingInput,
    ): Recording? {
        if (input.sessionId != null) {
            requireNotNull(sessionService.findById(input.sessionId))
        }

        if (input.templateId != null) {
            requireNotNull(templateService.findById(input.templateId))
        }

        val recordingEntity = recordingRepository.findByIdOrNull(id) ?: return null
        val recording =
            recordingRepository.save(
                recordingEntity.copy(
                    sessionId = input.sessionId ?: recordingEntity.sessionId,
                    templateId = input.templateId ?: recordingEntity.templateId,
                    args = input.args.let { jacksonObjectMapper().writeValueAsString(it) } ?: recordingEntity.args,
                    secret = input.secret ?: recordingEntity.secret,
                    lookBackSecs = input.lookBackSecs ?: recordingEntity.lookBackSecs,
                    timeoutSecs = input.timeoutSecs ?: recordingEntity.timeoutSecs,
                    recording = input.recording ?: recordingEntity.recording,
                    status = input.status ?: recordingEntity.status,
                ),
            ).let(::toDomainObject)
        publisher.publishEvent(RecordingsUpdatedEvent(this, listOf(recording)))
        return recording
    }

    fun deleteById(id: Int): Boolean {
        val recording =
            recordingRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return false
        recordingRepository.deleteById(id)
        publisher.publishEvent(RecordingsDeletedEvent(this, listOf(recording)))
        return true
    }

    fun deleteBySessionIds(sessionIds: List<Int>): Boolean {
        val recordingEntities = recordingRepository.findBySessionIds(sessionIds)
        if (recordingEntities.isEmpty()) {
            return false
        }
        recordingRepository.deleteAll(recordingEntities)
        publisher.publishEvent(RecordingsDeletedEvent(this, recordingEntities.map(::toDomainObject)))
        return true
    }

    fun deleteByTemplateIds(templateIds: List<Int>): Boolean {
        val recordingEntities = recordingRepository.findByTemplateIds(templateIds)
        if (recordingEntities.isEmpty()) {
            return false
        }
        recordingRepository.deleteAll(recordingEntities)
        publisher.publishEvent(RecordingsDeletedEvent(this, recordingEntities.map(::toDomainObject)))
        return true
    }

    private fun toDomainObject(entity: RecordingEntity) =
        Recording(
            id = entity.id!!,
            sessionId = entity.sessionId,
            templateId = entity.templateId,
            args = jacksonObjectMapper().readValue<Map<String, String>>(entity.args),
            secret = entity.secret,
            lookBackSecs = entity.lookBackSecs,
            timeoutSecs = entity.timeoutSecs,
            recording = entity.recording,
            status = entity.status,
            createdAt =
                entity.createdAt!!
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
            updatedAt =
                entity.updatedAt
                    ?.atZone(ZoneId.systemDefault())
                    ?.toOffsetDateTime(),
        )

    data class StartRecordingInput(
        val templateId: Int,
        val args: Map<String, String>,
    )

    data class UpdateRecordingInput(
        val sessionId: Int?,
        val templateId: Int?,
        val args: Map<String, String>?,
        val secret: String?,
        val lookBackSecs: Int?,
        val timeoutSecs: Int?,
        val recording: String?,
        val status: StatusEnum?,
    )

    data class RecordingFilterInput(
        val sessionId: Int,
    )

    companion object {
        const val RECORDING_TIMEOUT = 120

        // Placeholder for future feature.
        const val LOOKBACK_SECS_PLACEHOLDER = 0
    }
}
