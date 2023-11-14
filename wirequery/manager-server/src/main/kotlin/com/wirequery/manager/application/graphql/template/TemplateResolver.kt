// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.template

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.wirequery.manager.domain.template.Template
import com.wirequery.manager.domain.template.TemplateService
import com.wirequery.manager.domain.template.TemplateService.CreateTemplateInput
import com.wirequery.manager.domain.template.TemplateService.UpdateTemplateInput
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
@PreAuthorize("isAuthenticated()")
class TemplateResolver(
    private val templateService: TemplateService,
) {
    @DgsQuery
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_TEMPLATES.name())")
    fun template(id: Int): Template? {
        return templateService.findById(id)
    }

    @DgsQuery
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_TEMPLATES.name())")
    fun templates(): Iterable<Template> {
        return templateService.findAll()
    }

    @DgsMutation
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).CREATE_TEMPLATE.name()) &&
        (!#input.allowUserInitiation || hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).USER_AUTH_TEMPLATE.name()))""",
    )
    fun createTemplate(input: CreateTemplateInput): Template {
        return templateService.create(input)
    }

    @DgsMutation
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).UPDATE_TEMPLATE.name()) &&
        (!#input.allowUserInitiation || hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).USER_AUTH_TEMPLATE.name()))""",
    )
    fun updateTemplate(
        id: Int,
        input: UpdateTemplateInput,
    ): Template? {
        return templateService.update(id, input)
    }

    @DgsMutation
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).DELETE_TEMPLATE.name())")
    fun deleteTemplate(id: Int): Boolean {
        return templateService.deleteById(id)
    }
}
