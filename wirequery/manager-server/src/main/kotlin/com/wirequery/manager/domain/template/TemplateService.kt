package com.wirequery.manager.domain.template

import com.wirequery.manager.domain.template.TemplateEvent.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId

@Service
@Transactional
class TemplateService(
    private val templateRepository: TemplateRepository,
    private val publisher: ApplicationEventPublisher,
) {
    fun findById(id: Int): Template? {
        return templateRepository.findByIdOrNull(id)
            ?.let(::toDomainObject)
    }

    fun findByIds(ids: Iterable<Int>): List<Template> {
        return templateRepository.findByIds(ids)
            .map(::toDomainObject)
    }

    fun findAll(): List<Template> {
        return templateRepository.findAll()
            .map(::toDomainObject)
    }

    fun create(input: CreateTemplateInput): Template {
        val templateEntity =
            TemplateEntity(
                name = input.name,
                description = input.description,
                fields =
                    input.fields.map {
                        TemplateEntity.FieldEntity(
                            key = it.key,
                            label = it.label,
                            type = it.type,
                        )
                    },
                nameTemplate = input.nameTemplate,
                allowUserInitiation = input.allowUserInitiation,
                descriptionTemplate = input.descriptionTemplate,
            )
        val template = toDomainObject(templateRepository.save(templateEntity))
        publisher.publishEvent(TemplatesCreatedEvent(this, listOf(template)))
        return template
    }

    fun update(
        id: Int,
        input: UpdateTemplateInput,
    ): Template? {
        val templateEntity = templateRepository.findByIdOrNull(id) ?: return null
        val template =
            templateRepository.save(
                templateEntity.copy(
                    name = input.name ?: templateEntity.name,
                    description = input.description ?: templateEntity.description,
                    fields =
                        input.fields?.map {
                            TemplateEntity.FieldEntity(
                                key = it.key,
                                label = it.label,
                                type = it.type,
                            )
                        } ?: templateEntity.fields,
                    nameTemplate = input.nameTemplate ?: templateEntity.nameTemplate,
                    descriptionTemplate = input.descriptionTemplate ?: templateEntity.descriptionTemplate,
                    allowUserInitiation = input.allowUserInitiation ?: templateEntity.allowUserInitiation,
                ),
            ).let(::toDomainObject)
        publisher.publishEvent(TemplatesUpdatedEvent(this, listOf(template)))
        return template
    }

    fun deleteById(id: Int): Boolean {
        val template =
            templateRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return false
        templateRepository.deleteById(id)
        publisher.publishEvent(TemplatesDeletedEvent(this, listOf(template)))
        return true
    }

    private fun toDomainObject(entity: TemplateEntity) =
        Template(
            id = entity.id!!,
            name = entity.name,
            description = entity.description,
            fields = entity.fields.map(::fieldToDomainObject),
            nameTemplate = entity.nameTemplate,
            descriptionTemplate = entity.descriptionTemplate,
            allowUserInitiation = entity.allowUserInitiation,
            createdAt =
                entity.createdAt!!
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
            updatedAt =
                entity.updatedAt
                    ?.atZone(ZoneId.systemDefault())
                    ?.toOffsetDateTime(),
            createdBy = entity.createdBy,
            updatedBy = entity.updatedBy,
        )

    private fun fieldToDomainObject(entity: TemplateEntity.FieldEntity) =
        Template.Field(
            key = entity.key,
            label = entity.label,
            type = entity.type,
        )

    data class CreateTemplateInput(
        val name: String,
        val description: String,
        val fields: List<Template.Field>,
        val nameTemplate: String,
        val descriptionTemplate: String,
        val allowUserInitiation: Boolean,
    )

    data class UpdateTemplateInput(
        val name: String?,
        val description: String?,
        val fields: List<Template.Field>?,
        val nameTemplate: String?,
        val descriptionTemplate: String?,
        val allowUserInitiation: Boolean?,
    )
}
