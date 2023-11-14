// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.recording

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.recording.RecordingFixtures.RECORDING_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.recording.RecordingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        RecordingResolver::class,
        GraphQLExceptionHandler::class,
        RecordingService::class,
    ],
)
class RecordingResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var recordingService: RecordingService

    @Test
    fun `recordings fetches sessions when user has VIEW_SESSIONS authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_SESSIONS.name }))

        whenever(recordingService.findAll(RecordingService.RecordingFilterInput(RECORDING_FIXTURE_WITH_ID_1.sessionId)))
            .thenReturn(listOf(RECORDING_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ recordings(filter: { sessionId: ${RECORDING_FIXTURE_WITH_ID_1.sessionId} }) { id } }",
                "data.recordings[*].id",
            )

        assertThat(ids).contains(RECORDING_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `recordings does not fetch sessions when user does not have VIEW_SESSIONS authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ recordings { id } }",
                    "data.recordings[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(recordingService, times(0)).findAll(any())
    }
}
