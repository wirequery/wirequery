package com.wirequery.manager.domain.session

import com.wirequery.manager.domain.FunctionalException.Companion.functionalError
import com.wirequery.manager.domain.session.SessionEvent.SessionsCreatedEvent
import com.wirequery.manager.domain.session.SessionEvent.SessionsDeletedEvent
import com.wirequery.manager.domain.storedquery.StoredQueryService
import com.wirequery.manager.domain.storedquery.StoredQueryService.CreateStoredQueryInput
import com.wirequery.manager.domain.template.Template
import com.wirequery.manager.domain.template.Template.FieldType.*
import com.wirequery.manager.domain.template.TemplateService
import com.wirequery.manager.domain.templatequery.TemplateQueryService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.jvm.optionals.getOrNull

@Service
@Transactional
class SessionService(
    private val templateService: TemplateService,
    private val templateQueryService: TemplateQueryService,
    private val storedQueryService: StoredQueryService,
    private val querySessionRepository: QuerySessionRepository,
    private val publisher: ApplicationEventPublisher,
) {
    fun findById(id: Int): Session? {
        return querySessionRepository.findByIdOrNull(id)
            ?.let(::toDomainObject)
    }

    fun findByIds(ids: Iterable<Int>): List<Session> {
        return querySessionRepository.findByIds(ids)
            .map(::toDomainObject)
    }

    fun findAll(): List<Session> {
        return querySessionRepository.findByNonDraft()
            .map(::toDomainObject)
    }

    fun create(
        input: CreateSessionInput,
        draft: Boolean,
    ): Session {
        val template =
            templateService.findById(input.templateId)
                ?: functionalError("Unable to find template: ${input.templateId}")

        val sessionEntity =
            SessionEntity(
                name = interpolate(template.nameTemplate, template.fields, input.variables),
                description = interpolate(template.descriptionTemplate, template.fields, input.variables),
                draft = draft,
            )

        val session = toDomainObject(querySessionRepository.save(sessionEntity))

        templateQueryService.findByTemplateIds(listOf(input.templateId))
            .forEach { templateQuery ->
                storedQueryService.create(
                    CreateStoredQueryInput(
                        sessionId = session.id,
                        name = interpolate(templateQuery.nameTemplate, template.fields, input.variables),
                        type = "TAPPING",
                        query = interpolate(templateQuery.queryTemplate, template.fields, input.variables),
                        queryLimit = templateQuery.queryLimit,
                        endDate = input.endDate,
                    ),
                )
            }

        publisher.publishEvent(SessionsCreatedEvent(this, listOf(session), mapOf(session to input.templateId)))
        return session
    }

    fun deleteOldDrafts() {
        val drafts = querySessionRepository.findDrafts()
        // TODO events?
        querySessionRepository.deleteAll(drafts)
    }

    // TODO test the different cases.
    private fun interpolate(
        template: String,
        fields: List<Template.Field>,
        variables: List<CreateSessionInputFieldValue>,
    ): String {
        var updatedTemplate = template
        fields.forEach { field ->
            val variable = variables.singleOrNull { it.key == field.key }
            if (variable != null) {
                updatedTemplate =
                    when (field.type) {
                        TEXT, TEXTAREA -> {
                            updatedTemplate.replace("{{${field.key}}}", "\"" + variable.value.replace("\"", "\\\"") + "\"")
                        }

                        BOOLEAN -> {
                            updatedTemplate.replace("{{${field.key}}}", "" + (variable.value == "true"))
                        }

                        FLOAT -> {
                            updatedTemplate.replace("{{${field.key}}}", "" + variable.value.toFloat())
                        }

                        INTEGER -> {
                            updatedTemplate.replace("{{${field.key}}}", "" + variable.value.toInt())
                        }
                    }
            }
        }
        return updatedTemplate
    }

    fun stopCapturing(
        sessionId: Int,
        publish: Boolean,
    ) {
        if (publish) {
            querySessionRepository.findById(sessionId).getOrNull()?.let {
                querySessionRepository.save(it.copy(draft = false))
            }
        }
        val storedQueries = storedQueryService.findBySessionIds(listOf(sessionId))
        storedQueryService.stopQueryingByApplicationIds(storedQueries.map { it.applicationId })
    }

    fun deleteById(id: Int): Boolean {
        val session =
            querySessionRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return false
        querySessionRepository.deleteById(id)
        publisher.publishEvent(SessionsDeletedEvent(this, listOf(session)))
        return true
    }

    private fun toDomainObject(entity: SessionEntity) =
        Session(
            id = entity.id!!,
            name = entity.name,
            description = entity.description,
            createdAt =
                entity.createdAt!!
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
            updatedAt =
                entity.updatedAt
                    ?.atZone(ZoneId.systemDefault())
                    ?.toOffsetDateTime(),
            draft = entity.draft,
            createdBy = entity.createdBy,
            updatedBy = entity.updatedBy,
        )

    data class CreateSessionInput(
        val templateId: Int,
        val variables: List<CreateSessionInputFieldValue>,
        val endDate: OffsetDateTime,
    )

    data class CreateSessionInputFieldValue(
        val key: String,
        val value: String,
    )
}
