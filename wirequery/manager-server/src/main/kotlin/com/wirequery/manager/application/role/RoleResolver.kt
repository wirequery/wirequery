// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.role

import com.netflix.graphql.dgs.*
import com.wirequery.manager.domain.authorisation.Authorisation
import com.wirequery.manager.domain.authorisation.AuthorisationService
import com.wirequery.manager.domain.role.Role
import com.wirequery.manager.domain.role.RoleService
import com.wirequery.manager.domain.role.RoleService.CreateRoleInput
import com.wirequery.manager.domain.role.RoleService.UpdateRoleInput
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
@PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).MANAGE_ROLES.name())")
class RoleResolver(
    private val roleService: RoleService,
    private val authorisationService: AuthorisationService,
) {
    @DgsQuery
    fun role(id: Int): Role? {
        return roleService.findById(id)
    }

    @DgsQuery
    fun roles(): Iterable<Role> {
        return roleService.findAll()
    }

    @DgsMutation
    fun createRole(input: CreateRoleInput): Role {
        return roleService.create(input)
    }

    @DgsMutation
    fun updateRole(
        id: Int,
        input: UpdateRoleInput,
    ): Role? {
        return roleService.update(id, input)
    }

    @DgsMutation
    fun deleteRole(id: Int): Boolean {
        return roleService.deleteById(id)
    }

    @DgsData(parentType = "Role")
    fun authorisations(dfe: DgsDataFetchingEnvironment): List<Authorisation> {
        val role = dfe.getSource<Role>()
        return authorisationService.findByNames(role.authorisationNames)
    }
}
