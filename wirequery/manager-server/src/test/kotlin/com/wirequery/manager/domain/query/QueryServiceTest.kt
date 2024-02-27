// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.query

import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.global.FakePubSubService
import com.wirequery.manager.domain.global.PubSubService
import com.wirequery.manager.domain.query.AggregatorService.Aggregator
import com.wirequery.manager.domain.query.QueryEvent.QueryEnteredEvent
import com.wirequery.manager.domain.query.QueryEvent.QueryReportedEvent
import com.wirequery.manager.domain.query.QueryService.QueryMutation
import com.wirequery.manager.domain.query.QueryService.QueryMutation.AddQueryMutation
import com.wirequery.manager.domain.tenant.TenantService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import reactor.core.Disposable
import reactor.core.publisher.FluxSink
import java.time.Clock

@ExtendWith(MockitoExtension::class)
class QueryServiceTest {
    @Mock
    private lateinit var queryIdGenerator: QueryIdGenerator
    private lateinit var queryParserService: QueryParserService
    private lateinit var pubSubService: PubSubService

    @Mock
    private lateinit var clock: Clock

    @Mock
    private lateinit var aggregatorService: AggregatorService

    @Mock
    private lateinit var applicationService: ApplicationService

    @Mock
    private lateinit var tenantService: TenantService

    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    private lateinit var queryService: QueryService

    @BeforeEach
    fun setUp() {
        lenient().`when`(queryIdGenerator.generateId()).thenReturn(SOME_QUERY_ID)
        pubSubService = FakePubSubService()
        queryParserService = QueryParserService()
        queryService =
            QueryService(
                queryIdGenerator,
                queryParserService,
                clock,
                pubSubService,
                aggregatorService,
                applicationService,
                publisher,
                tenantService,
            )
    }

    @Test
    fun `startQuerying fails if the app is quarantined and does not publish a query addition`() {
        whenever(applicationService.isQuarantined(DEFAULT_APP_NAME))
            .thenReturn(true)

        val sink = mock<FluxSink<AddQueryMutation>>()
        pubSubService.subscribe("query_additions:$DEFAULT_APP_NAME", sink, AddQueryMutation::class.java)

        val exception =
            assertThrows<FunctionalException> {
                queryService.startQuerying(SOME_QUERY_REPORT.queryId, DEFAULT_APP_NAME)
            }

        verify(sink, times(0)).next(any<AddQueryMutation>())

        assertThat(exception.message).isEqualTo("Application is in quarantine: $DEFAULT_APP_NAME")
    }

    @Test
    fun `startQuerying checks if the app is quarantined before publishing a query addition`() {
        whenever(applicationService.isQuarantined(DEFAULT_APP_NAME))
            .thenReturn(false)

        val sink = mock<FluxSink<AddQueryMutation>>()
        pubSubService.subscribe("query_additions:$DEFAULT_APP_NAME", sink, AddQueryMutation::class.java)

        queryService.startQuerying(SOME_QUERY_REPORT.queryId, DEFAULT_APP_NAME)

        verify(sink).next(any<AddQueryMutation>())
    }

    @Test
    fun `query wont run if isQuarantined`() {
        val sink = mock<FluxSink<QueryReport>>()
        whenever(applicationService.isQuarantined(DEFAULT_APP_NAME))
            .thenReturn(true)

        whenever(queryIdGenerator.generateId()).thenReturn("123")

        queryService.query(DEFAULT_APP_NAME, sink)

        verify(sink).next(
            QueryReport(
                appName = "",
                queryId = "123",
                message = "{\"error\":\"Application is in quarantine: $DEFAULT_APP_NAME\"}",
                startTime = 0,
                endTime = 0,
                traceId = null,
                requestCorrelationId = null,
            ),
        )
    }

    @Test
    fun `query publishes QueryEnteredEvent`() {
        val sink = mock<FluxSink<QueryReport>>()
        whenever(aggregatorService.create(any()))
            .thenReturn(Aggregator { it })
        queryService.query(DEFAULT_APP_NAME, sink)
        verify(publisher).publishEvent(QueryEnteredEvent(queryService, DEFAULT_APP_NAME))
    }

    @Test
    @Disabled // TODO fix
    fun `query subscribes to query reports for same app name and sends event`() {
        val sink = mock<FluxSink<QueryReport>>()
        whenever(aggregatorService.create(any()))
            .thenReturn(Aggregator { it })
        queryService.query(DEFAULT_APP_NAME, sink)
        verify(sink).next(SOME_QUERY_REPORT)
        verify(publisher).publishEvent(QueryReportedEvent(queryService, SOME_QUERY_REPORT))
    }

    @Test
    @Disabled // TODO fix
    fun `query results are transformed by aggregator`() {
        val sink = mock<FluxSink<QueryReport>>()
        whenever(aggregatorService.create(any()))
            .thenReturn(Aggregator { it.copy(message = "some other message") })
        queryService.query(DEFAULT_APP_NAME, sink)
        verify(sink).next(SOME_QUERY_REPORT.copy(message = "some other message"))
    }

    @Test
    fun `query does not return null aggregated results`() {
        val sink = mock<FluxSink<QueryReport>>()
        whenever(aggregatorService.create(any()))
            .thenReturn(Aggregator { null })
        queryService.query(DEFAULT_APP_NAME, sink)
        verify(sink, times(0)).next(any())
    }

    @Test
    fun `query does not subscribe to query reports for different query ids`() {
        val sink = mock<FluxSink<QueryReport>>()
        whenever(aggregatorService.create(any()))
            .thenReturn(Aggregator { it })
        queryService.query(DEFAULT_APP_NAME, sink)
        verify(sink, times(0)).next(any())
    }

    @Test
    fun `query notifies listeners for queries`() {
        val sink = mock<FluxSink<QueryMutation>>()
        whenever(aggregatorService.create(any()))
            .thenReturn(Aggregator { it })
        queryService.subscribeToQueries(DEFAULT_APP_NAME, sink)
        queryService.query(DEFAULT_APP_NAME, mock())
        verify(sink).next(
            AddQueryMutation(
                queryId = SOME_QUERY_ID,
                queryHead =
                    QueryParserService.QueryHead(
                        appName = DEFAULT_APP_NAME,
                        method = "",
                        path = "",
                        statusCode = "",
                    ),
                operations = listOf(),
                aggregatorOperation = null,
            ),
        )
    }

    @Test
    fun `query does not notify non-listeners for queries`() {
        val sink = mock<FluxSink<QueryMutation>>()
        whenever(aggregatorService.create(any()))
            .thenReturn(Aggregator { it })
        queryService.subscribeToQueries(OTHER_APP_NAME, sink)
        queryService.query(DEFAULT_APP_NAME, mock())
        verify(sink, times(0)).next(any())
    }

    @Test
    fun `query sends remove query on dispose`() {
        val queryReportSink = mock<FluxSink<QueryReport>>()
        val queryRemovalSink = mock<FluxSink<QueryMutation>>()
        whenever(aggregatorService.create(any()))
            .thenReturn(Aggregator { it })
        pubSubService.subscribe("query_removals:$DEFAULT_APP_NAME", queryRemovalSink, QueryMutation::class.java)
        queryService.query(DEFAULT_APP_NAME, queryReportSink)
        val disposable = argumentCaptor<Disposable>()
        verify(queryReportSink, atLeastOnce()).onDispose(disposable.capture())
        disposable.firstValue.dispose()
        verify(queryRemovalSink).next(QueryMutation.RemoveQueryMutationById(SOME_QUERY_ID))
    }

    private companion object {
        const val SOME_QUERY_ID = "mock-id"
        const val SOME_QUERY_ID_2 = "mock-id-2"
        const val DEFAULT_APP_NAME = "default"
        const val OTHER_APP_NAME = "other-app"

        val SOME_QUERY_REPORT =
            QueryReport(
                appName = DEFAULT_APP_NAME,
                queryId = SOME_QUERY_ID,
                message = "{}",
                startTime = 10,
                endTime = 20,
                traceId = "abc",
                requestCorrelationId = null,
            )
    }
}
