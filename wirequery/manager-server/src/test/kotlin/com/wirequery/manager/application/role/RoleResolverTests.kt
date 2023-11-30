// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.role

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.authorisation.AuthorisationService
import com.wirequery.manager.domain.role.RoleFixtures.CREATE_ROLE_FIXTURE_1
import com.wirequery.manager.domain.role.RoleFixtures.ROLE_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.role.RoleFixtures.UPDATE_ROLE_FIXTURE_1
import com.wirequery.manager.domain.role.RoleService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.GrantedAuthority

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        RoleResolver::class,
        RoleDataLoader::class,
        GraphQLExceptionHandler::class,
    ],
)
class RoleResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var roleService: RoleService

    @MockBean
    private lateinit var authorisationService: AuthorisationService

    @Test
    fun `roles fetches role if user has MANAGE_ROLES authority`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.MANAGE_ROLES.name }))

        whenever(roleService.findById(1))
            .thenReturn(ROLE_FIXTURE_WITH_ID_1)

        val id =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "{ role(id: 1) { id } }",
                "data.role.id",
            )

        assertThat(id).isEqualTo(ROLE_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `role does not fetch role if user does not have MANAGE_ROLES authority`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ role(id: 1) { id } }",
                    "data.role.id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(roleService, times(0)).findById(anyInt())
    }

    @Test
    fun `roles fetches roles if user has MANAGE_ROLES authority`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.MANAGE_ROLES.name }))

        whenever(roleService.findAll())
            .thenReturn(listOf(ROLE_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ roles { id } }",
                "data.roles[*].id",
            )

        assertThat(ids).contains(ROLE_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `roles does not fetch roles if user does not have MANAGE_ROLES authority`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ roles { id } }",
                    "data.roles[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(roleService, times(0)).findAll()
    }

    @Test
    fun `createRole calls create if user has MANAGE_ROLES authority`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.MANAGE_ROLES.name }))

        whenever(roleService.create(any()))
            .thenReturn(ROLE_FIXTURE_WITH_ID_1)

        val createRoleInput =
            mapOf(
                "name" to "Some role",
                "authorisationNames" to listOf("Some authorisation"),
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation createRole(\$input: CreateRoleInput!) { createRole(input: \$input) { id } }",
                "data.createRole.id",
                mapOf("input" to createRoleInput),
            )

        assertThat(result).isEqualTo(ROLE_FIXTURE_WITH_ID_1.id.toString())

        verify(roleService).create(CREATE_ROLE_FIXTURE_1)
    }

    @Test
    fun `createRole does not call create if does not have MANAGE_ROLES authority`() {
        val createRoleInput =
            mapOf(
                "name" to "Some name",
                "authorisationNames" to listOf("Some authorisation"),
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation createRole(\$input: CreateRoleInput!) { createRole(input: \$input) { id } }",
                    "data.createRole.id",
                    mapOf("input" to createRoleInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(roleService, times(0)).create(CREATE_ROLE_FIXTURE_1)
    }

    @Test
    fun `updateRole calls update if user has MANAGE_ROLES authority`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.MANAGE_ROLES.name }))

        whenever(roleService.update(anyInt(), any()))
            .thenReturn(ROLE_FIXTURE_WITH_ID_1)

        val updateRoleInput =
            mapOf(
                "name" to "Some role",
                "authorisationNames" to listOf("Some authorisation"),
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation updateRole(\$id: ID!, \$input: UpdateRoleInput!) { updateRole(id: \$id, input: \$input) { id } }",
                "data.updateRole.id",
                mapOf("id" to ROLE_FIXTURE_WITH_ID_1.id, "input" to updateRoleInput),
            )

        assertThat(result).isEqualTo(ROLE_FIXTURE_WITH_ID_1.id.toString())

        verify(roleService).update(ROLE_FIXTURE_WITH_ID_1.id, UPDATE_ROLE_FIXTURE_1)
    }

    @Test
    fun `updateRole does not call update if user does not have MANAGE_ROLES authority`() {
        val updateRoleInput =
            mapOf(
                "name" to "Some name",
                "authorisationNames" to listOf("Some authorisation"),
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation updateRole(\$id: ID!, \$input: UpdateRoleInput!) { updateRole(id: \$id, input: \$input) { id } }",
                    "data.updateRole.id",
                    mapOf("id" to ROLE_FIXTURE_WITH_ID_1.id, "input" to updateRoleInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(roleService, times(0)).update(ROLE_FIXTURE_WITH_ID_1.id, UPDATE_ROLE_FIXTURE_1)
    }

    @Test
    fun `deleteRole calls delete if user has MANAGE_ROLES authorisations`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.MANAGE_ROLES.name }))

        whenever(roleService.deleteById(any()))
            .thenReturn(true)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                "mutation deleteRole(\$id: ID!) { deleteRole(id: \$id) }",
                "data.deleteRole",
                mapOf("id" to 1),
            )

        assertThat(result).isEqualTo(true)

        verify(roleService).deleteById(1)
    }

    @Test
    fun `deleteRole does not call delete if user does not have MANAGE_ROLES authorisations`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                    "mutation deleteRole(\$id: ID!) { deleteRole(id: \$id) }",
                    "data.deleteRole",
                    mapOf("id" to 1),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(roleService, times(0)).deleteById(anyInt())
    }
}
