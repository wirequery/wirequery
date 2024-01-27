// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring5

import com.fasterxml.jackson.databind.ObjectMapper
import com.wirequery.core.TraceableQuery
import com.wirequery.core.query.ContextMapCreator
import com.wirequery.core.query.QueryCompiler
import com.wirequery.core.query.QueryEvaluator
import com.wirequery.core.query.context.CompiledQuery
import com.wirequery.core.query.context.Query
import com.wirequery.core.query.context.Query.Operation
import com.wirequery.core.query.context.QueryHead
import io.grpc.stub.StreamObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import wirequerypb.Wirequery
import wirequerypb.Wirequery.*
import wirequerypb.WirequeryServiceGrpc

@ExtendWith(MockitoExtension::class)
internal class WireQueryAdapterTest {
    @Mock
    private lateinit var wireQueryStub: WirequeryServiceGrpc.WirequeryServiceStub

    @Mock
    private lateinit var connectionSettings: WireQueryAdapter.ConnectionSettings

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @Mock
    private lateinit var queryCompiler: QueryCompiler

    @Mock
    private lateinit var logger: Logger

    @Mock
    private lateinit var traceCache: TraceCache

    @Mock
    private lateinit var contextMapCreator: ContextMapCreator

    @Mock
    private lateinit var sleeper: Sleeper

    @InjectMocks
    private lateinit var wireQueryAdapter: WireQueryAdapter

    @Test
    fun `init will start listening for queries`() {
        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub)
            .listenForQueries(
                eq(
                    ListenForQueriesRequest.newBuilder()
                        .setAppName(connectionSettings.appName)
                        .setApiKey(connectionSettings.apiKey)
                        .build(),
                ),
                any(),
            )
    }

    @Test
    fun `when listening for queries and a new query comes in, it is added to the list of queries after mapping`() {
        val captor = argumentCaptor<StreamObserver<QueryMutation>>()
        val compiledQuery = mock<CompiledQuery>()

        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub).listenForQueries(any(), captor.capture())

        whenever(queryCompiler.compile(SOME_QUERY))
            .thenReturn(compiledQuery)

        captor.firstValue.onNext(SOME_PROTO_ADD_NEW_QUERY)

        assertThat(wireQueryAdapter.getQueries())
            .isEqualTo(listOf(TraceableQuery(queryId = SOME_QUERY_ID_1, compiledQuery = compiledQuery)))
    }

    @Test
    fun `when an error occurs while listening for queries, the error is reported`() {
        val captor = argumentCaptor<StreamObserver<QueryMutation>>()

        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub).listenForQueries(any(), captor.capture())

        whenever(queryCompiler.compile(SOME_QUERY))
            .thenThrow(RuntimeException(SOME_ERROR_MESSAGE))

        whenever(objectMapper.writeValueAsString(mapOf("error" to SOME_ERROR_MESSAGE)))
            .thenReturn(SOME_ERROR)

        captor.firstValue.onNext(SOME_PROTO_ADD_NEW_QUERY)

        wireQueryAdapter.schedulePublishing()

        verify(wireQueryStub).reportQueryResults(
            eq(
                QueryReports.newBuilder()
                    .setApiKey(SOME_API_KEY)
                    .setAppName(SOME_APP_NAME)
                    .addQueryReports(
                        QueryReport.newBuilder()
                            .setQueryId(SOME_PROTO_ADD_NEW_QUERY.addQuery.queryId)
                            .setMessage(SOME_ERROR)
                            .build(),
                    )
                    .build(),
            ),
            any(),
        )
    }

    @Test
    fun `blank cel expressions are converted to null`() {
        val captor = argumentCaptor<StreamObserver<QueryMutation>>()
        val compiledQuery = mock<CompiledQuery>()

        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub).listenForQueries(any(), captor.capture())

        whenever(queryCompiler.compile(SOME_QUERY_WITH_BLANK_CEL_EXPRESSIONS))
            .thenReturn(compiledQuery)

        captor.firstValue.onNext(SOME_PROTO_ADD_NEW_QUERY_EMPTY_CEL_EXPRESSIONS)

        assertThat(wireQueryAdapter.getQueries())
            .isEqualTo(listOf(TraceableQuery(queryId = SOME_QUERY_ID_1, compiledQuery = compiledQuery)))
    }

    @Test
    fun `when the function name is not set, aggregatorOperation is set to null`() {
        val captor = argumentCaptor<StreamObserver<QueryMutation>>()
        val compiledQuery = mock<CompiledQuery>()

        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub).listenForQueries(any(), captor.capture())

        whenever(queryCompiler.compile(SOME_QUERY_WITH_BLANK_AGGREGATOR))
            .thenReturn(compiledQuery)

        captor.firstValue.onNext(SOME_PROTO_ADD_NEW_QUERY_BLANK_AGGREGATOR)

        assertThat(wireQueryAdapter.getQueries())
            .isEqualTo(listOf(TraceableQuery(queryId = SOME_QUERY_ID_1, compiledQuery = compiledQuery)))
    }

    @Test
    fun `queries are deleted for remove query by id`() {
        val captor = argumentCaptor<StreamObserver<QueryMutation>>()
        val compiledQuery = mock<CompiledQuery>()

        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub).listenForQueries(any(), captor.capture())

        whenever(queryCompiler.compile(SOME_QUERY))
            .thenReturn(compiledQuery)

        captor.firstValue.onNext(SOME_PROTO_ADD_NEW_QUERY)

        captor.firstValue.onNext(SOME_PROTO_REMOVE_QUERY_BY_ID)

        assertThat(wireQueryAdapter.getQueries())
            .isEqualTo(emptyList<TraceableQuery>())
    }

    @Test
    fun `results are not sent back by trace id if nothing by trace id is found`() {
        val captor = argumentCaptor<StreamObserver<QueryMutation>>()

        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub).listenForQueries(any(), captor.capture())

        captor.firstValue.onNext(SOME_PROTO_QUERY_ONE_TRACE)

        verify(wireQueryStub, times(0))
            .reportQueryResults(any(), any())
    }

    @Test
    fun `results are sent back by trace id when requesting results by trace id`() {
        val captor = argumentCaptor<StreamObserver<QueryMutation>>()

        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub).listenForQueries(any(), captor.capture())

        val mockInterceptedRequestResponse = mock<QueryEvaluator.InterceptedRequestResponse>()
        whenever(mockInterceptedRequestResponse.startTime)
            .thenReturn(10)

        whenever(mockInterceptedRequestResponse.endTime)
            .thenReturn(20)

        whenever(mockInterceptedRequestResponse.traceId)
            .thenReturn(SOME_TRACE_ID)

        whenever(contextMapCreator.createMaskedContextMap(mockInterceptedRequestResponse))
            .thenReturn(mapOf("something" to SOME_RESULT))

        whenever(objectMapper.writeValueAsString(mapOf("result" to mapOf("something" to SOME_RESULT))))
            .thenReturn(SOME_MESSAGE)

        whenever(traceCache.findByTraceId(SOME_TRACE_ID))
            .thenReturn(mockInterceptedRequestResponse)

        captor.firstValue.onNext(SOME_PROTO_QUERY_ONE_TRACE)

        verify(wireQueryStub)
            .reportQueryResults(
                eq(
                    QueryReports.newBuilder()
                        .setApiKey(SOME_API_KEY)
                        .setAppName(SOME_APP_NAME)
                        .addQueryReports(
                            QueryReport.newBuilder()
                                .setQueryId(SOME_QUERY_ID_1)
                                .setStartTime(10)
                                .setEndTime(20)
                                .setTraceId(SOME_TRACE_ID)
                                .setMessage(SOME_MESSAGE)
                                .build(),
                        )
                        .build(),
                ),
                any(),
            )
    }

    @Test
    fun `listening starts again 5 seconds after an error`() {
        val captor = argumentCaptor<StreamObserver<QueryMutation>>()

        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub).listenForQueries(any(), captor.capture())

        captor.firstValue.onError(RuntimeException("ERROR"))

        verify(sleeper).sleep(5000)

        verify(wireQueryStub, times(2)).listenForQueries(any(), any())
    }

    @Test
    fun `listening starts again 5 seconds after a completion`() {
        val captor = argumentCaptor<StreamObserver<QueryMutation>>()

        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        wireQueryAdapter.init()

        verify(wireQueryStub).listenForQueries(any(), captor.capture())

        captor.firstValue.onCompleted()

        verify(sleeper).sleep(5000)

        verify(wireQueryStub, times(2)).listenForQueries(any(), any())
    }

    @Test
    fun `published results are not published when publishResult is called, but not schedulePublishing`() {
        whenever(objectMapper.writeValueAsString(mapOf("result" to SOME_RESULT)))
            .thenReturn(SOME_MESSAGE)

        wireQueryAdapter.publishResult(TraceableQuery(SOME_QUERY_ID_1, mock()), SOME_RESULT, 0, 0, null)

        verify(wireQueryStub, times(0))
            .reportQueryResults(any(), any())
    }

    @Test
    fun `published errors are not published when publishResult is called, but not schedulePublishing`() {
        whenever(objectMapper.writeValueAsString(mapOf("error" to SOME_ERROR)))
            .thenReturn(SOME_MESSAGE)

        wireQueryAdapter.publishError(SOME_QUERY_ID_1, SOME_ERROR, 0, 0, null)

        verify(wireQueryStub, times(0))
            .reportQueryResults(any(), any())
    }

    @Test
    fun `published results and errors are published when schedulePublishing is called`() {
        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        whenever(objectMapper.writeValueAsString(mapOf("result" to SOME_RESULT)))
            .thenReturn(SOME_MESSAGE)

        whenever(objectMapper.writeValueAsString(mapOf("error" to SOME_ERROR)))
            .thenReturn(SOME_ERROR_MESSAGE)

        wireQueryAdapter.publishResult(TraceableQuery(SOME_QUERY_ID_1, mock()), SOME_RESULT, 0, 0, null)
        wireQueryAdapter.publishError(SOME_QUERY_ID_2, SOME_ERROR, 0, 0, null)

        wireQueryAdapter.schedulePublishing()

        val resultQueryReport =
            QueryReport.newBuilder()
                .setQueryId(SOME_QUERY_ID_1)
                .setMessage(SOME_MESSAGE)
                .build()

        val errorQueryReport =
            QueryReport.newBuilder()
                .setQueryId(SOME_QUERY_ID_2)
                .setMessage(SOME_ERROR_MESSAGE)
                .build()

        verify(wireQueryStub)
            .reportQueryResults(
                eq(
                    QueryReports.newBuilder()
                        .setApiKey(SOME_API_KEY)
                        .setAppName(SOME_APP_NAME)
                        .addQueryReports(resultQueryReport)
                        .addQueryReports(errorQueryReport)
                        .build(),
                ),
                any(),
            )
    }

    @Test
    fun `schedulePublishing will publish zero times when called with empty values`() {
        wireQueryAdapter.schedulePublishing()

        verify(wireQueryStub, times(0))
            .reportQueryResults(any(), any())
    }

    @Test
    fun `schedulePublishing will publish once when calling with no published messages and then with one directly afterwards`() {
        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        whenever(objectMapper.writeValueAsString(mapOf("result" to SOME_RESULT)))
            .thenReturn(SOME_MESSAGE)

        wireQueryAdapter.schedulePublishing()

        wireQueryAdapter.publishResult(TraceableQuery(SOME_QUERY_ID_1, mock()), SOME_RESULT, 0, 0, null)
        wireQueryAdapter.schedulePublishing()

        val resultQueryReport =
            QueryReport.newBuilder()
                .setQueryId(SOME_QUERY_ID_1)
                .setMessage(SOME_MESSAGE)
                .build()

        verify(wireQueryStub)
            .reportQueryResults(
                eq(
                    QueryReports.newBuilder()
                        .setApiKey(SOME_API_KEY)
                        .setAppName(SOME_APP_NAME)
                        .addQueryReports(resultQueryReport)
                        .build(),
                ),
                any(),
            )
    }

    @Test
    fun `schedulePublishing will publish once when called four times`() {
        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        whenever(objectMapper.writeValueAsString(mapOf("result" to SOME_RESULT)))
            .thenReturn(SOME_MESSAGE)

        wireQueryAdapter.publishResult(TraceableQuery(SOME_QUERY_ID_1, mock()), SOME_RESULT, 0, 0, null)
        wireQueryAdapter.schedulePublishing()

        wireQueryAdapter.publishResult(TraceableQuery(SOME_QUERY_ID_1, mock()), SOME_RESULT, 0, 0, null)
        wireQueryAdapter.schedulePublishing()

        wireQueryAdapter.schedulePublishing()
        wireQueryAdapter.schedulePublishing()

        val queryReport =
            QueryReport.newBuilder()
                .setQueryId(SOME_QUERY_ID_1)
                .setMessage(SOME_MESSAGE)
                .build()

        verify(wireQueryStub, times(1))
            .reportQueryResults(
                eq(
                    QueryReports.newBuilder()
                        .setApiKey(SOME_API_KEY)
                        .setAppName(SOME_APP_NAME)
                        .addQueryReports(queryReport)
                        .build(),
                ),
                any(),
            )
    }

    @Test
    fun `schedulePublishing will publish twice when called five times`() {
        whenever(connectionSettings.appName)
            .thenReturn(SOME_APP_NAME)

        whenever(connectionSettings.apiKey)
            .thenReturn(SOME_API_KEY)

        whenever(objectMapper.writeValueAsString(mapOf("result" to SOME_RESULT)))
            .thenReturn(SOME_MESSAGE)

        wireQueryAdapter.publishResult(TraceableQuery(SOME_QUERY_ID_1, mock()), SOME_RESULT, 0, 0, null)
        wireQueryAdapter.schedulePublishing()

        wireQueryAdapter.publishResult(TraceableQuery(SOME_QUERY_ID_1, mock()), SOME_RESULT, 0, 0, null)
        wireQueryAdapter.schedulePublishing()

        wireQueryAdapter.schedulePublishing()
        wireQueryAdapter.schedulePublishing()
        wireQueryAdapter.schedulePublishing()

        val queryReport =
            QueryReport.newBuilder()
                .setQueryId(SOME_QUERY_ID_1)
                .setMessage(SOME_MESSAGE)
                .build()

        verify(wireQueryStub, times(2))
            .reportQueryResults(
                eq(
                    QueryReports.newBuilder()
                        .setApiKey(SOME_API_KEY)
                        .setAppName(SOME_APP_NAME)
                        .addQueryReports(queryReport)
                        .build(),
                ),
                any(),
            )
    }

    private companion object {
        const val SOME_APP_NAME = "some-app-name"
        const val SOME_API_KEY = "some-app-key"
        const val SOME_QUERY_ID_1 = "some-query-id-1"
        const val SOME_QUERY_ID_2 = "some-query-id-2"
        const val SOME_NAME_1 = "some-name-1"
        const val SOME_NAME_2 = "some-name-2"
        const val SOME_TRACE_ID = "some-trace-id"
        const val SOME_CEL_EXPRESSION_1 = "some-cel-expression-1"
        const val SOME_CEL_EXPRESSION_2 = "some-cel-expression-2"
        const val SOME_METHOD = "some-method"
        const val SOME_PATH = "some-path"
        const val SOME_STATUS_CODE = "some-status-code"
        const val SOME_RESULT = "some-result"
        const val SOME_ERROR = "some-error"
        const val SOME_MESSAGE = "some-message"
        const val SOME_ERROR_MESSAGE = "some-error-message"

        val SOME_QUERY =
            Query(
                QueryHead(
                    method = SOME_METHOD,
                    path = SOME_PATH,
                    statusCode = SOME_STATUS_CODE,
                ),
                streamOperations = listOf(Operation(name = SOME_NAME_1, celExpression = SOME_CEL_EXPRESSION_1)),
                aggregatorOperation = Operation(name = SOME_NAME_2, celExpression = SOME_CEL_EXPRESSION_2),
            )

        val SOME_QUERY_WITH_BLANK_CEL_EXPRESSIONS =
            Query(
                QueryHead(
                    method = SOME_METHOD,
                    path = SOME_PATH,
                    statusCode = SOME_STATUS_CODE,
                ),
                streamOperations = listOf(Operation(name = SOME_NAME_1, celExpression = null)),
                aggregatorOperation = Operation(name = SOME_NAME_2, celExpression = null),
            )

        val SOME_QUERY_WITH_BLANK_AGGREGATOR =
            Query(
                QueryHead(
                    method = SOME_METHOD,
                    path = SOME_PATH,
                    statusCode = SOME_STATUS_CODE,
                ),
                streamOperations = listOf(),
                aggregatorOperation = null,
            )

        val SOME_PROTO_ADD_NEW_QUERY: QueryMutation =
            QueryMutation.newBuilder()
                .setAddQuery(
                    Wirequery.Query.newBuilder()
                        .setQueryId(SOME_QUERY_ID_1)
                        .setQueryHead(
                            Wirequery.QueryHead.newBuilder()
                                .setAppName(SOME_APP_NAME)
                                .setMethod(SOME_METHOD)
                                .setPath(SOME_PATH)
                                .setStatusCode(SOME_STATUS_CODE)
                                .build(),
                        )
                        .addStreamOperations(
                            Wirequery.Operation.newBuilder()
                                .setName(SOME_NAME_1)
                                .setCelExpression(SOME_CEL_EXPRESSION_1)
                                .build(),
                        )
                        .setAggregatorOperation(
                            Wirequery.Operation.newBuilder()
                                .setName(SOME_NAME_2)
                                .setCelExpression(SOME_CEL_EXPRESSION_2)
                                .build(),
                        )
                        .build(),
                )
                .build()

        val SOME_PROTO_ADD_NEW_QUERY_EMPTY_CEL_EXPRESSIONS: QueryMutation =
            QueryMutation.newBuilder()
                .setAddQuery(
                    Wirequery.Query.newBuilder()
                        .setQueryId(SOME_QUERY_ID_1)
                        .setQueryHead(
                            Wirequery.QueryHead.newBuilder()
                                .setAppName(SOME_APP_NAME)
                                .setMethod(SOME_METHOD)
                                .setPath(SOME_PATH)
                                .setStatusCode(SOME_STATUS_CODE)
                                .build(),
                        )
                        .addStreamOperations(
                            Wirequery.Operation.newBuilder()
                                .setName(SOME_NAME_1)
                                .setCelExpression(" ")
                                .build(),
                        )
                        .setAggregatorOperation(
                            Wirequery.Operation.newBuilder()
                                .setName(SOME_NAME_2)
                                .setCelExpression(" ")
                                .build(),
                        )
                        .build(),
                )
                .build()

        val SOME_PROTO_ADD_NEW_QUERY_BLANK_AGGREGATOR: QueryMutation =
            QueryMutation.newBuilder()
                .setAddQuery(
                    Wirequery.Query.newBuilder()
                        .setQueryId(SOME_QUERY_ID_1)
                        .setQueryHead(
                            Wirequery.QueryHead.newBuilder()
                                .setAppName(SOME_APP_NAME)
                                .setMethod(SOME_METHOD)
                                .setPath(SOME_PATH)
                                .setStatusCode(SOME_STATUS_CODE)
                                .build(),
                        )
                        .build(),
                )
                .build()

        val SOME_PROTO_REMOVE_QUERY_BY_ID: QueryMutation =
            QueryMutation.newBuilder()
                .setRemoveQueryById(SOME_QUERY_ID_1)
                .build()

        val SOME_PROTO_QUERY_ONE_TRACE: QueryMutation =
            QueryMutation.newBuilder()
                .setQueryOneTrace(
                    QueryOneTrace.newBuilder()
                        .setQueryId(SOME_QUERY_ID_1)
                        .setTraceId(SOME_TRACE_ID)
                        .build(),
                )
                .build()
    }
}
