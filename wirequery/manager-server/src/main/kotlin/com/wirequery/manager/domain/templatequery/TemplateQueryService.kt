// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.templatequery

import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.template.TemplateService
import com.wirequery.manager.domain.templatequery.TemplateQueryEvent.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId

@Service
@Transactional
class TemplateQueryService(
    private val queryParserService: QueryParserService,
    private val templateQueryRepository: TemplateQueryRepository,
    private val templateService: TemplateService,
    private val applicationService: ApplicationService,
    private val publisher: ApplicationEventPublisher,
) {
    fun findById(id: Int): TemplateQuery? {
        return templateQueryRepository.findByIdOrNull(id)
            ?.let(::toDomainObject)
    }

    fun findByIds(ids: Iterable<Int>): List<TemplateQuery> {
        return templateQueryRepository.findByIds(ids)
            .map(::toDomainObject)
    }

    fun findByTemplateIds(templateIds: Iterable<Int>): List<TemplateQuery> {
        return templateQueryRepository.findByTemplateIds(templateIds)
            .map(::toDomainObject)
    }

    fun findByApplicationIds(applicationIds: Iterable<Int>): List<TemplateQuery> {
        return templateQueryRepository.findByApplicationIds(applicationIds)
            .map(::toDomainObject)
    }

    fun findAll(filterInput: TemplateQueryFilterInput? = null): List<TemplateQuery> {
        if (filterInput?.templateId != null) {
            return templateQueryRepository.findByTemplateIds(listOf(filterInput.templateId))
                .map(::toDomainObject)
        }

        if (filterInput?.applicationId != null) {
            return templateQueryRepository.findByApplicationIds(listOf(filterInput.applicationId))
                .map(::toDomainObject)
        }

        return templateQueryRepository.findAll()
            .map(::toDomainObject)
    }

    fun create(input: CreateTemplateQueryInput): TemplateQuery {
        requireNotNull(templateService.findById(input.templateId))

        val appName = queryParserService.parse(input.queryTemplate).queryHead.appName
        val application =
            applicationService.findByName(appName)
                ?: throw FunctionalException("Application not found: $appName")

        val templateQueryEntity =
            TemplateQueryEntity(
                templateId = input.templateId,
                applicationId = application.id,
                nameTemplate = input.nameTemplate,
                queryTemplate = input.queryTemplate,
                queryLimit = input.queryLimit,
            )
        val templateQuery = toDomainObject(templateQueryRepository.save(templateQueryEntity))
        publisher.publishEvent(TemplateQuerysCreatedEvent(this, listOf(templateQuery)))
        return templateQuery
    }

    fun update(
        id: Int,
        input: UpdateTemplateQueryInput,
    ): TemplateQuery? {
        if (input.templateId != null) {
            requireNotNull(templateService.findById(input.templateId))
        }

        val application =
            if (input.queryTemplate != null) {
                val appName = queryParserService.parse(input.queryTemplate).queryHead.appName
                applicationService.findByName(appName)
                    ?: throw FunctionalException("Application not found: $appName")
            } else {
                null
            }

        val templateQueryEntity = templateQueryRepository.findByIdOrNull(id) ?: return null
        val templateQuery =
            templateQueryRepository.save(
                templateQueryEntity.copy(
                    templateId = input.templateId ?: templateQueryEntity.templateId,
                    applicationId = application?.id ?: templateQueryEntity.applicationId,
                    nameTemplate = input.nameTemplate ?: templateQueryEntity.nameTemplate,
                    queryTemplate = input.queryTemplate,
                    queryLimit = input.queryLimit ?: templateQueryEntity.queryLimit,
                ),
            ).let(::toDomainObject)
        publisher.publishEvent(TemplateQuerysUpdatedEvent(this, listOf(templateQuery)))
        return templateQuery
    }

    fun deleteById(id: Int): Boolean {
        val templateQuery =
            templateQueryRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return false
        templateQueryRepository.deleteById(id)
        publisher.publishEvent(TemplateQuerysDeletedEvent(this, listOf(templateQuery)))
        return true
    }

    fun deleteByTemplateIds(templateIds: List<Int>): Boolean {
        val templateQueryEntities = templateQueryRepository.findByTemplateIds(templateIds)
        if (templateQueryEntities.isEmpty()) {
            return false
        }
        templateQueryRepository.deleteAll(templateQueryEntities)
        publisher.publishEvent(TemplateQuerysDeletedEvent(this, templateQueryEntities.map(::toDomainObject)))
        return true
    }

    fun deleteByApplicationIds(applicationIds: List<Int>): Boolean {
        val templateQueryEntities = templateQueryRepository.findByApplicationIds(applicationIds)
        if (templateQueryEntities.isEmpty()) {
            return false
        }
        templateQueryRepository.deleteAll(templateQueryEntities)
        publisher.publishEvent(TemplateQuerysDeletedEvent(this, templateQueryEntities.map(::toDomainObject)))
        return true
    }

    private fun toDomainObject(entity: TemplateQueryEntity) =
        TemplateQuery(
            id = entity.id!!,
            templateId = entity.templateId,
            applicationId = entity.applicationId,
            nameTemplate = entity.nameTemplate,
            queryTemplate = entity.queryTemplate,
            queryLimit = entity.queryLimit,
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

    data class CreateTemplateQueryInput(
        val templateId: Int,
        val nameTemplate: String,
        val queryTemplate: String,
        val queryLimit: Int,
    )

    data class UpdateTemplateQueryInput(
        val templateId: Int?,
        val nameTemplate: String?,
        // This should always be passed since it is used in the PreAuthorize clause.
        val queryTemplate: String,
        val queryLimit: Int?,
    )

    data class TemplateQueryFilterInput(
        val templateId: Int? = null,
        val applicationId: Int? = null,
    )
}
