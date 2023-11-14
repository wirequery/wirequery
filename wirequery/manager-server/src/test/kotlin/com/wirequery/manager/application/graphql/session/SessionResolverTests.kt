// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.session

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.session.SessionFixtures.CREATE_SESSION_FIXTURE_1
import com.wirequery.manager.domain.session.SessionFixtures.SESSION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.session.SessionService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
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
        SessionResolver::class,
        SessionDataLoader::class,
        GraphQLExceptionHandler::class,
    ],
)
class SessionResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var sessionService: SessionService

    @Test
    fun `session fetches session when user has VIEW_SESSIONS group authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_SESSIONS.name }))

        whenever(sessionService.findById(SESSION_FIXTURE_WITH_ID_1.id))
            .thenReturn(SESSION_FIXTURE_WITH_ID_1)

        val id =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "{ session(id: ${SESSION_FIXTURE_WITH_ID_1.id}) { id } }",
                "data.session.id",
            )

        assertThat(id).isEqualTo(SESSION_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `session does not fetch session when user does not have VIEW_SESSIONS group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ session(id: ${SESSION_FIXTURE_WITH_ID_1.id}) { id } }",
                    "data.session.id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(sessionService, times(0)).findById(anyInt())
    }

    @Test
    fun `sessions fetches sessions when user has VIEW_SESSIONS group authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_SESSIONS.name }))

        whenever(sessionService.findAll())
            .thenReturn(listOf(SESSION_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ sessions { id } }",
                "data.sessions[*].id",
            )

        assertThat(ids).contains(SESSION_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `sessions does not fetch sessions when user does not have VIEW_SESSIONS authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ sessions { id } }",
                    "data.sessions[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(sessionService, times(0)).findAll()
    }

    @Test
    fun `createSession calls createSession if user has CREATE_SESSION authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.CREATE_SESSION.name }))

        whenever(sessionService.create(any(), eq(false)))
            .thenReturn(SESSION_FIXTURE_WITH_ID_1)

        val createSessionInput =
            mapOf(
                "templateId" to CREATE_SESSION_FIXTURE_1.templateId,
                "variables" to
                    listOf(
                        mapOf(
                            "key" to CREATE_SESSION_FIXTURE_1.variables[0].key,
                            "value" to CREATE_SESSION_FIXTURE_1.variables[0].value,
                        ),
                    ),
                "endDate" to "2100-01-01T00:00:00Z",
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation createSession(\$input: CreateSessionInput!) { createSession(input: \$input) { id } }",
                "data.createSession.id",
                mapOf("input" to createSessionInput),
            )

        assertThat(result).isEqualTo(SESSION_FIXTURE_WITH_ID_1.id.toString())

        verify(sessionService).create(any(), eq(false))
    }

    @Test
    fun `createSession does not call createSession if user does not have CREATE_SESSION authorisation`() {
        val createSessionInput =
            mapOf(
                "templateId" to CREATE_SESSION_FIXTURE_1.templateId,
                "variables" to
                    listOf(
                        mapOf(
                            "key" to CREATE_SESSION_FIXTURE_1.variables[0].key,
                            "value" to CREATE_SESSION_FIXTURE_1.variables[0].value,
                        ),
                    ),
                "endDate" to "2100-01-01T00:00:00Z",
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation createSession(\$input: CreateSessionInput!) { createSession(input: \$input) { id } }",
                    "data.createSession.id",
                    mapOf("input" to createSessionInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(sessionService, times(0)).create(any(), eq(false))
    }

    @Test
    fun `deleteSession deletes session if user has DELETE_SESSION authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.DELETE_SESSION.name }))

        whenever(sessionService.deleteById(any()))
            .thenReturn(true)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                "mutation deleteSession(\$id: ID!) { deleteSession(id: \$id) }",
                "data.deleteSession",
                mapOf("id" to 1),
            )

        assertThat(result).isEqualTo(true)

        verify(sessionService).deleteById(1)
    }

    @Test
    fun `deleteSession does not delete session if user does not have DELETE_SESSION authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                    "mutation deleteSession(\$id: ID!) { deleteSession(id: \$id) }",
                    "data.deleteSession",
                    mapOf("id" to 1),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(sessionService, times(0)).deleteById(anyInt())
    }
}
