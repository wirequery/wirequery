// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.querylog

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.application.graphql.storedquery.StoredQueryByIdDataLoader
import com.wirequery.manager.application.graphql.storedquery.StoredQueryResolver
import com.wirequery.manager.application.graphql.storedquery.StoredQuerysByApplicationIdDataLoader
import com.wirequery.manager.application.graphql.storedquery.StoredQuerysBySessionIdDataLoader
import com.wirequery.manager.domain.access.AccessService
import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum.VIEW_STORED_QUERY
import com.wirequery.manager.domain.querylog.QueryLogFixtures.QUERY_LOG_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.querylog.QueryLogService
import com.wirequery.manager.domain.querylog.QueryLogService.QueryLogFilterInput
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.STORED_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryService
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
        StoredQueryResolver::class,
        StoredQueryByIdDataLoader::class,
        StoredQuerysByApplicationIdDataLoader::class,
        StoredQuerysBySessionIdDataLoader::class,
        QueryLogResolver::class,
        QueryLogsByStoredQueryIdDataLoader::class,
        AccessService::class,
        GraphQLExceptionHandler::class,
    ],
)
class QueryLogResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var storedQueryService: StoredQueryService

    @MockBean
    private lateinit var accessService: AccessService

    @MockBean
    private lateinit var queryLogService: QueryLogService

    @Test
    fun `queryLogs are fetched when user has VIEW_STORED_QUERY group authorisation`() {
        whenever(
            accessService.isAuthorisedByStoredQueryId(
                QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                VIEW_STORED_QUERY,
            ),
        ).thenReturn(true)

        whenever(queryLogService.findMainLogs(QueryLogFilterInput(storedQueryId = QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))
            .thenReturn(listOf(QUERY_LOG_FIXTURE_WITH_ID_1))

        val messages =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ queryLogs(filter: { storedQueryId: ${QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId} }) { message } }",
                "data.queryLogs[*].message",
            )

        assertThat(messages).hasSize(1)
    }

    @Test
    fun `queryLogs are not fetched when user does not have VIEW_STORED_QUERY group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ queryLogs(filter: { storedQueryId: ${QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId} }) { message } }",
                    "data.queryLogs[*].message",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(queryLogService, times(0)).findMainLogs(any())
    }

    @Test
    fun `queryLogByTrace are fetched when user has VIEW_STORED_QUERY group authorisation`() {
        whenever(
            accessService.isAuthorisedByStoredQueryId(
                QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                VIEW_STORED_QUERY,
            ),
        ).thenReturn(true)

        whenever(queryLogService.findByTraceId(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId, QUERY_LOG_FIXTURE_WITH_ID_1.traceId!!))
            .thenReturn(listOf(QUERY_LOG_FIXTURE_WITH_ID_1))

        val messages =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ queryLogByTrace(filter: { traceId: \"${QUERY_LOG_FIXTURE_WITH_ID_1.traceId}\", storedQueryId: ${QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId} }) { message } }",
                "data.queryLogByTrace[*].message",
            )

        assertThat(messages).hasSize(1)
    }

    @Test
    fun `queryLogByTrace are not fetched when user does not have VIEW_STORED_QUERY group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ queryLogByTrace(filter: { traceId: \"${QUERY_LOG_FIXTURE_WITH_ID_1.traceId}\", storedQueryId: ${QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId} }) { message } }",
                    "data.queryLogByTrace[*].message",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(queryLogService, times(0)).findMainLogs(any())
    }

    @Test
    fun `queryLogs_storedQuery fetches when user has VIEW_STORED_QUERY group authorisation`() {
        whenever(
            accessService.isAuthorisedByStoredQueryId(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId, VIEW_STORED_QUERY),
        ).thenReturn(true)

        whenever(
            accessService.isAuthorisedByStoredQueryIds(
                setOf(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId),
                VIEW_STORED_QUERY,
            ),
        ).thenReturn(true)

        whenever(queryLogService.findMainLogs(QueryLogFilterInput(storedQueryId = QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))
            .thenReturn(listOf(QUERY_LOG_FIXTURE_WITH_ID_1))

        whenever(storedQueryService.findByIds(setOf(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1.copy(id = QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ queryLogs(filter: { storedQueryId: ${QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId} }) { storedQuery { id } } }",
                "data.queryLogs[*].storedQuery.id",
            )

        assertThat(ids).contains(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId.toString())
    }

    @Test
    fun `queryLogs_storedQuery are not fetched when user does not have VIEW_STORED_QUERY group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ queryLogs(filter: { storedQueryId: ${QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId} }) { storedQuery { id } } }",
                    "data.queryLogs[*].storedQuery.id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(queryLogService, times(0)).findMainLogs(any())

        verify(storedQueryService, times(0)).findByIds(any())
    }

    @Test
    fun `storedQuerys_queryLogs are fetched when user has VIEW_STORED_QUERIES group authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_STORED_QUERIES.name }))

        whenever(
            accessService.isAuthorisedByStoredQueryIds(
                setOf(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId),
                VIEW_STORED_QUERY,
            ),
        ).thenReturn(true)

        whenever(storedQueryService.findAll())
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1.copy(id = QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))

        whenever(queryLogService.findMainLogsByStoredQueryIds(setOf(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))
            .thenReturn(listOf(QUERY_LOG_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ storedQuerys { queryLogs { message } } }",
                "data.storedQuerys[*].queryLogs[*].message",
            )

        assertThat(ids).hasSize(1)
    }

    @Test
    fun `storedQuerys_queryLogs are not fetched when user does not have VIEW_STORED_QUERIES group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ storedQuerys { queryLogs { message } } }",
                    "data.storedQuerys[*].queryLogs[*].message",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(storedQueryService, times(0)).findAll()

        verify(queryLogService, times(0)).findMainLogsByStoredQueryIds(any())
    }

    // TODO add tests with at least one stored query -> queryLogs for PreAuthorize?
    // TODO any tests missing?
}
