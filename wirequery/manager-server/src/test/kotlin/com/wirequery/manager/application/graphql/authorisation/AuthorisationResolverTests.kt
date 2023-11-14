// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.authorisation

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.domain.authorisation.Authorisation
import com.wirequery.manager.domain.authorisation.AuthorisationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        AuthorisationResolver::class,
        GraphQLExceptionHandler::class,
    ],
)
class AuthorisationResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var authorisationService: AuthorisationService

    @Test
    fun `authorisations fetches all authorisations`() {
        whenever(authorisationService.findAll())
            .thenReturn(
                listOf(
                    Authorisation(
                        "name",
                        "label",
                        "description",
                    ),
                ),
            )

        val names =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ authorisations { name label description } }",
                "data.authorisations[*].name",
            )

        assertThat(names).contains("name")
    }

    @Test
    fun `authorisations does not fetch all authorisations if the user is not authenticated`() {
        val securityContext = mock<SecurityContext>()
        SecurityContextHolder.setContext(securityContext)

        assertThrows<QueryException> {
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ authorisations { name label description } }",
                "data.authorisations[*].name",
            )
        }

        verify(authorisationService, times(0)).findAll()
    }
}
