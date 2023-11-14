// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.templatequery

import com.netflix.graphql.dgs.*
import com.wirequery.manager.domain.application.Application
import com.wirequery.manager.domain.template.Template
import com.wirequery.manager.domain.templatequery.TemplateQuery
import com.wirequery.manager.domain.templatequery.TemplateQueryService
import com.wirequery.manager.domain.templatequery.TemplateQueryService.*
import org.springframework.security.access.prepost.PreAuthorize
import java.util.concurrent.CompletableFuture

@DgsComponent
@PreAuthorize("isAuthenticated()")
class TemplateQueryResolver(
    private val templateQueryService: TemplateQueryService,
) {
    @DgsQuery
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_TEMPLATES.name())")
    fun templateQuery(id: Int): TemplateQuery? {
        return templateQueryService.findById(id)
    }

    @DgsQuery
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_TEMPLATES.name())")
    fun templateQuerys(filter: TemplateQueryFilterInput?): Iterable<TemplateQuery> {
        return templateQueryService.findAll(filter)
    }

    @DgsMutation
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).CREATE_TEMPLATE.name())
            && @accessService.isExpressionTemplateAllowed(#input.queryTemplate, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).CREATE_OR_EDIT_TEMPLATE_QUERY)""",
    )
    fun createTemplateQuery(input: CreateTemplateQueryInput): TemplateQuery {
        return templateQueryService.create(input)
    }

    @DgsMutation
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).UPDATE_TEMPLATE.name())
            && @accessService.isExpressionTemplateAllowed(#input.queryTemplate, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).CREATE_OR_EDIT_TEMPLATE_QUERY)""",
    )
    fun updateTemplateQuery(
        id: Int,
        input: UpdateTemplateQueryInput,
    ): TemplateQuery? {
        return templateQueryService.update(id, input)
    }

    @DgsMutation
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).DELETE_TEMPLATE.name())")
    fun deleteTemplateQuery(id: Int): Boolean {
        return templateQueryService.deleteById(id)
    }

    @DgsData(parentType = "TemplateQuery")
    fun template(dfe: DgsDataFetchingEnvironment): CompletableFuture<Template?> {
        val templateQuery = dfe.getSource<TemplateQuery>()
        return dfe.getDataLoader<Int, Template?>("templateById")
            .load(templateQuery.templateId)
    }

    @DgsData(parentType = "Template", field = "templateQuerys")
    fun templateQuerysByTemplate(dfe: DgsDataFetchingEnvironment): CompletableFuture<Iterable<TemplateQuery>> {
        val template = dfe.getSource<Template>()
        val templateId = template.id
        return dfe.getDataLoader<Int, Iterable<TemplateQuery>>("templateQuerysByTemplateId")
            .load(templateId)
            .thenApply { it ?: listOf() }
    }

    @DgsData(parentType = "TemplateQuery")
    fun application(dfe: DgsDataFetchingEnvironment): CompletableFuture<Application?> {
        val templateQuery = dfe.getSource<TemplateQuery>()
        return dfe.getDataLoader<Int, Application?>("applicationById")
            .load(templateQuery.applicationId)
    }

    @DgsData(parentType = "Application", field = "templateQuerys")
    fun templateQuerysByApplication(dfe: DgsDataFetchingEnvironment): CompletableFuture<Iterable<TemplateQuery>> {
        val application = dfe.getSource<Application>()
        val applicationId = application.id
        return dfe.getDataLoader<Int, Iterable<TemplateQuery>>("templateQuerysByApplicationId")
            .load(applicationId)
            .thenApply { it ?: listOf() }
    }
}
