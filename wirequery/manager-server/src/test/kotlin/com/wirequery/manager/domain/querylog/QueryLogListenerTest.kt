// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.global.PubSubService
import com.wirequery.manager.domain.query.QueryEvent
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.query.QueryReport
import com.wirequery.manager.domain.query.QueryService.QueryMutation.QueryOneTrace
import com.wirequery.manager.domain.storedquery.StoredQuery
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.STORED_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryService
import com.wirequery.manager.domain.storedquery.StoredQueryService.Companion.STORED_QUERY_PREFIX
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class QueryLogListenerTest {
    @Mock
    private lateinit var queryLogService: QueryLogService

    @Mock
    private lateinit var storedQueryService: StoredQueryService

    @Mock
    private lateinit var queryParserService: QueryParserService

    @Mock
    private lateinit var pubSubService: PubSubService

    @InjectMocks
    private lateinit var queryLogListener: QueryLogListener

    @Test
    fun `onEvent QueryReportedEvent ignores events that don't start with STORED_QUERY_PREFIX`() {
        queryLogListener.onEvent(
            QueryEvent.QueryReportedEvent(
                this,
                QueryReport(
                    appName = APPLICATION_FIXTURE_WITH_ID_1.name,
                    queryId = "",
                    message = "{}",
                    startTime = 10L,
                    endTime = 20L,
                    traceId = "abc",
                    requestCorrelationId = "def",
                ),
            ),
        )

        verify(queryLogService, times(0))
            .logOrDisableIfLimitsReached(any())
    }

    @Test
    fun `onEvent QueryReportedEvent writes a log event for query ids starting with the STORED_QUERY_PREFIX and re-traces if type is QUERY_WITH_TRACING`() {
        val queryIdWithPrefix = STORED_QUERY_PREFIX + STORED_QUERY_FIXTURE_WITH_ID_1.id

        whenever(storedQueryService.findById(STORED_QUERY_FIXTURE_WITH_ID_1.id))
            .thenReturn(STORED_QUERY_FIXTURE_WITH_ID_1.copy(type = StoredQuery.Type.QUERY_WITH_TRACING))

        queryLogListener.onEvent(
            QueryEvent.QueryReportedEvent(
                this,
                QueryReport(
                    appName = APPLICATION_FIXTURE_WITH_ID_1.name,
                    queryId = queryIdWithPrefix,
                    message = "{}",
                    startTime = 10L,
                    endTime = 20L,
                    traceId = "abc",
                    requestCorrelationId = "def",
                ),
            ),
        )

        verify(queryLogService).logOrDisableIfLimitsReached(
            QueryLogService.CreateQueryLogInput(
                storedQueryId = STORED_QUERY_FIXTURE_WITH_ID_1.id,
                message = "{}",
                startTime = 10L,
                endTime = 20L,
                traceId = "abc",
                requestCorrelationId = "def",
                appName = "SomeName",
            ),
        )

        verify(pubSubService)
            .publish(
                "trace_requests",
                QueryOneTrace("$queryIdWithPrefix:trace", "abc"),
            )
    }

    @Test
    fun `onEvent QueryReportedEvent writes trace if id ends with trace`() {
        val queryIdWithPrefix = STORED_QUERY_PREFIX + STORED_QUERY_FIXTURE_WITH_ID_1.id + ":trace"

        val queryReport =
            QueryReport(
                appName = APPLICATION_FIXTURE_WITH_ID_1.name,
                queryId = queryIdWithPrefix,
                message = "{}",
                startTime = 10L,
                endTime = 20L,
                traceId = "abc",
                requestCorrelationId = "def",
            )

        queryLogListener.onEvent(QueryEvent.QueryReportedEvent(this, queryReport))

        verify(queryLogService).putTrace(queryReport)

        verify(pubSubService, times(0))
            .publish(any(), any(), any())

        verifyNoMoreInteractions(storedQueryService, queryParserService)
    }

    @Test
    fun `onEvent QueryReportedEvent writes a log event for query ids starting with the STORED_QUERY_PREFIX and does not re-trace if type is not QUERY_WITH_TRACING`() {
        val queryIdWithPrefix = STORED_QUERY_PREFIX + STORED_QUERY_FIXTURE_WITH_ID_1.id

        whenever(storedQueryService.findById(STORED_QUERY_FIXTURE_WITH_ID_1.id))
            .thenReturn(STORED_QUERY_FIXTURE_WITH_ID_1)

        queryLogListener.onEvent(
            QueryEvent.QueryReportedEvent(
                this,
                QueryReport(
                    appName = APPLICATION_FIXTURE_WITH_ID_1.name,
                    queryId = queryIdWithPrefix,
                    message = "{}",
                    startTime = 10L,
                    endTime = 20L,
                    traceId = "abc",
                    requestCorrelationId = "def",
                ),
            ),
        )

        verify(queryLogService).logOrDisableIfLimitsReached(
            QueryLogService.CreateQueryLogInput(
                storedQueryId = STORED_QUERY_FIXTURE_WITH_ID_1.id,
                message = "{}",
                startTime = 10L,
                endTime = 20L,
                traceId = "abc",
                requestCorrelationId = "def",
                appName = "SomeName",
            ),
        )

        verify(pubSubService, times(0))
            .publish(any(), any(), any())
    }
}
