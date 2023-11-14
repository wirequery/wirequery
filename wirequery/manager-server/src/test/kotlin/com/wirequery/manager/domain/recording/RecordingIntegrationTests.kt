// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.recording.RecordingFixtures.START_RECORDING_FIXTURE_1
import com.wirequery.manager.domain.recording.RecordingFixtures.UPDATE_RECORDING_FIXTURE_1
import com.wirequery.manager.domain.session.QuerySessionRepository
import com.wirequery.manager.domain.session.SessionFixtures.SESSION_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.session.SessionService
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.template.TemplateRepository
import com.wirequery.manager.domain.template.TemplateService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class RecordingIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var recordingService: RecordingService

    @Autowired
    private lateinit var sessionService: SessionService

    @Autowired
    private lateinit var querySessionRepository: QuerySessionRepository

    @Autowired
    private lateinit var templateService: TemplateService

    @Autowired
    private lateinit var templateRepository: TemplateRepository

    @Test
    fun `Recordings cannot be created if related entities do not exist`() {
        assertThrows<RuntimeException> {
            recordingService.startRecording(START_RECORDING_FIXTURE_1)
        }
    }

    @Test
    fun `Recordings can be created, updated, fetched and deleted`() {
        val session = querySessionRepository.save(SESSION_ENTITY_FIXTURE_1)

        val template = templateRepository.save(TEMPLATE_ENTITY_FIXTURE_1)

        var recording =
            recordingService.startRecording(
                START_RECORDING_FIXTURE_1.copy(
                    templateId = template.id!!,
                ),
            )
        recording =
            recordingService.update(
                recording.id,
                UPDATE_RECORDING_FIXTURE_1.copy(
                    sessionId = session.id!!,
                    templateId = template.id,
                ),
            )!!

        assertThat(recordingService.findById(recording.id)).isNotNull
        assertThat(recordingService.findByIds(listOf(recording.id))).isNotEmpty

        assertThat(recordingService.findAll(RecordingService.RecordingFilterInput(sessionId = session.id!!))).isNotEmpty
        assertThat(recordingService.findBySessionIds(listOf(session.id!!))).isNotEmpty

        assertThat(recordingService.findByTemplateIds(listOf(template.id!!))).isNotEmpty

        recordingService.deleteById(recording.id)

        assertThat(recordingService.findBySessionIds(listOf(session.id!!))).isEmpty()
    }

    @Test
    fun `Recordings are deleted when related Sessions are deleted`() {
        val session = querySessionRepository.save(SESSION_ENTITY_FIXTURE_1)
        val template = templateRepository.save(TEMPLATE_ENTITY_FIXTURE_1)

        val recording =
            recordingService.startRecording(
                START_RECORDING_FIXTURE_1.copy(
                    templateId = template.id!!,
                ),
            )

        sessionService.deleteById(recording.sessionId)

        assertThat(recordingService.findAll(RecordingService.RecordingFilterInput(session.id!!))).isEmpty()
    }

    @Test
    fun `Recordings are not deleted when related Templates are deleted`() {
        val template = templateRepository.save(TEMPLATE_ENTITY_FIXTURE_1)

        val recording =
            recordingService.startRecording(
                START_RECORDING_FIXTURE_1.copy(
                    templateId = template.id!!,
                ),
            )

        templateService.deleteById(recording.templateId!!)

        assertThat(recordingService.findAll(RecordingService.RecordingFilterInput(recording.sessionId))).isNotEmpty()
    }
}
