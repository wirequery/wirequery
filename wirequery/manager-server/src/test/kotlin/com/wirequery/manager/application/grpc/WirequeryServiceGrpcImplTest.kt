// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.grpc

import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.query.QueryReport
import com.wirequery.manager.domain.query.QueryReportService
import com.wirequery.manager.domain.query.QueryService
import com.wirequery.manager.domain.storedquery.StoredQueryService
import com.wirequery.manager.domain.tenant.TenantRequestContext
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.stub.StreamObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import reactor.core.publisher.FluxSink
import wirequerypb.Wirequery

@ExtendWith(MockitoExtension::class)
class WirequeryServiceGrpcImplTest {
    @Mock
    private lateinit var storedQueryService: StoredQueryService

    @Mock
    private lateinit var queryService: QueryService

    @Mock
    private lateinit var queryReportService: QueryReportService

    @Mock
    private lateinit var applicationService: ApplicationService

    @Mock
    private lateinit var queryParserService: QueryParserService

    @Mock
    private lateinit var tenantRequestContext: TenantRequestContext

    @InjectMocks
    private lateinit var wirequeryServiceGrpcImpl: WirequeryServiceGrpcImpl

    @Test
    fun `listenForQueries closes if unauthenticated`() {
        whenever(applicationService.isApiKeyValid(APP_NAME, API_KEY))
            .thenReturn(false)

        val request =
            Wirequery.ListenForQueriesRequest.newBuilder()
                .setAppName(APP_NAME)
                .setApiKey(API_KEY)
                .buildPartial()

        val responseObserver = mock<StreamObserver<Wirequery.QueryMutation>>()

        wirequeryServiceGrpcImpl.listenForQueries(request, responseObserver)

        val captor = argumentCaptor<StatusException>()

        verify(responseObserver).onError(captor.capture())

        assertThat(captor.allValues.single().status)
            .isEqualTo(Status.UNAUTHENTICATED)

        verifyNoMoreInteractions(responseObserver)
    }

    @Test
    fun `listenForQueries closes on error`() {
        val request =
            Wirequery.ListenForQueriesRequest.newBuilder()
                .setAppName(APP_NAME)
                .setApiKey(API_KEY)
                .buildPartial()

        whenever(applicationService.isApiKeyValid(APP_NAME, API_KEY))
            .thenReturn(true)

        val responseObserver = mock<StreamObserver<Wirequery.QueryMutation>>()

        wirequeryServiceGrpcImpl.listenForQueries(request, responseObserver)

        val captor = argumentCaptor<FluxSink<QueryService.QueryMutation>>()
        verify(queryService).subscribeToQueries(eq(APP_NAME), captor.capture())

        whenever(responseObserver.onNext(any())).thenThrow(RuntimeException("Connection closed"))

        captor.firstValue.next(
            QueryService.QueryMutation.AddQueryMutation(
                queryId = QUERY_ID,
                queryHead =
                    QueryParserService.QueryHead(
                        appName = APP_NAME,
                        method = METHOD,
                        path = PATH,
                        statusCode = STATUS_CODE,
                    ),
                operations = OPERATIONS,
                aggregatorOperation = AGGREGATOR_OPERATION,
            ),
        )

        verify(responseObserver).onCompleted()

        verifyNoMoreInteractions(responseObserver)

        // NOTE: call to sink.complete() is not tested, since the Flux is created within listenForQueries
        // and therefore cannot be easily mocked.
    }

    @Test
    fun `listenForQueries calls subscribeToQueries on the service`() {
        val request =
            Wirequery.ListenForQueriesRequest.newBuilder()
                .setAppName(APP_NAME)
                .setApiKey(API_KEY)
                .buildPartial()

        whenever(applicationService.isApiKeyValid(APP_NAME, API_KEY))
            .thenReturn(true)

        val responseObserver = mock<StreamObserver<Wirequery.QueryMutation>>()

        wirequeryServiceGrpcImpl.listenForQueries(request, responseObserver)

        val captor = argumentCaptor<FluxSink<QueryService.QueryMutation>>()
        verify(queryService).subscribeToQueries(eq(APP_NAME), captor.capture())

        captor.firstValue.next(
            QueryService.QueryMutation.AddQueryMutation(
                queryId = QUERY_ID,
                queryHead =
                    QueryParserService.QueryHead(
                        appName = APP_NAME,
                        method = METHOD,
                        path = PATH,
                        statusCode = STATUS_CODE,
                    ),
                operations = OPERATIONS,
                aggregatorOperation = AGGREGATOR_OPERATION,
            ),
        )
        verify(responseObserver).onNext(
            Wirequery.QueryMutation.newBuilder()
                .setAddQuery(
                    Wirequery.Query.newBuilder()
                        .setQueryId(QUERY_ID)
                        .setQueryHead(
                            Wirequery.QueryHead.newBuilder()
                                .setAppName(APP_NAME)
                                .setMethod(METHOD)
                                .setPath(PATH)
                                .setStatusCode(STATUS_CODE)
                                .build(),
                        )
                        .setAggregatorOperation(AGG_PROTO_EXPRESSION)
                        .addAllStreamOperations(listOf(PROTO_EXPRESSION)),
                )
                .build(),
        )

        verify(responseObserver, times(0)).onCompleted()

        verifyNoMoreInteractions(responseObserver)
    }

    // TODO add tests for completing and sending result back in case of reportQueryResults
    @Test
    fun `reportQueryResults closes connection when unauthenticated`() {
        whenever(applicationService.isApiKeyValid(APP_NAME, API_KEY))
            .thenReturn(false)

        val responseObserver = mock<StreamObserver<Wirequery.Empty>>()

        wirequeryServiceGrpcImpl
            .reportQueryResults(
                Wirequery.QueryReports.newBuilder()
                    .setAppName(APP_NAME)
                    .setApiKey(API_KEY)
                    .addQueryReports(
                        Wirequery.QueryReport.newBuilder()
                            .setQueryId(QUERY_ID)
                            .setMessage(MESSAGE)
                            .build(),
                    )
                    .build(),
                responseObserver,
            )

        verify(queryReportService, times(0)).reportQueryResults(any())

        val captor = argumentCaptor<StatusException>()

        verify(responseObserver).onError(captor.capture())

        assertThat(captor.allValues.single().status)
            .isEqualTo(Status.UNAUTHENTICATED)
    }

    @Test
    fun `reportQueryResults creates a StreamObserver that allows for passing QueryReports`() {
        whenever(applicationService.isApiKeyValid(APP_NAME, API_KEY))
            .thenReturn(true)

        wirequeryServiceGrpcImpl
            .reportQueryResults(
                Wirequery.QueryReports.newBuilder()
                    .setAppName(APP_NAME)
                    .setApiKey(API_KEY)
                    .addQueryReports(
                        Wirequery.QueryReport.newBuilder()
                            .setQueryId(QUERY_ID)
                            .setMessage(MESSAGE)
                            .setStartTime(START_TIME)
                            .setEndTime(END_TIME)
                            .setTraceId(TRACE_ID)
                            .setRequestCorrelationId(REQUEST_CORRELATION_ID)
                            .build(),
                    ).build(),
                mock(),
            )

        verify(queryReportService).reportQueryResults(listOf(QueryReport(APP_NAME, QUERY_ID, MESSAGE, START_TIME, END_TIME, TRACE_ID, REQUEST_CORRELATION_ID)))
    }

    private companion object {
        const val QUERY_ID = "some-query-id"
        const val APP_NAME = "some-app-name"
        const val API_KEY = "1/some-app-key"
        const val METHOD = "some-method"
        const val PATH = "some-path"
        const val STATUS_CODE = "some-status-code"
        const val MESSAGE = "some-message"
        const val START_TIME = 10L
        const val END_TIME = 20L
        const val TRACE_ID = "abc"
        const val REQUEST_CORRELATION_ID = "def"

        val OPERATIONS = listOf(QueryParserService.Operation(name = "filter", celExpression = "true"))
        val AGGREGATOR_OPERATION = QueryParserService.Operation(name = "distinct", celExpression = "")
        val AGG_PROTO_EXPRESSION =
            Wirequery.Operation.newBuilder()
                .setName("distinct")
                .setCelExpression("")
                .build()!!
        val PROTO_EXPRESSION =
            Wirequery.Operation.newBuilder()
                .setName("filter")
                .setCelExpression("true")
                .build()!!
    }
}
