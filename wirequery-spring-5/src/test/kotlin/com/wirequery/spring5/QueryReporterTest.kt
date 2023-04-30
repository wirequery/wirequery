package com.wirequery.spring5

import com.wirequery.core.QueryLoader
import com.wirequery.core.ResultPublisher
import com.wirequery.core.TraceableQuery
import com.wirequery.core.query.QueryEvaluator
import com.wirequery.core.query.context.CompiledQuery
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class QueryReporterTest {

    @Mock
    private lateinit var queryEvaluator: QueryEvaluator

    @Mock
    private lateinit var queryLoader: QueryLoader

    @Mock
    private lateinit var resultPublisher: ResultPublisher

    @Mock
    private lateinit var requestData: RequestData

    @InjectMocks
    private lateinit var queryReporter: QueryReporter

    @Test
    fun `intercepted traffic is mapped to the WireQuery domain, evaluated and the results published`() {
        val request = mock<ContentCachingRequestWrapper>()
        val response = mock<ContentCachingResponseWrapper>()

        whenever(request.method).thenReturn("GET")
        whenever(request.requestURI).thenReturn("/abc")

        whenever(request.headerNames)
            .thenReturn(Collections.enumeration(listOf("Accept")))

        whenever(request.getHeaders("Accept"))
            .thenReturn(Collections.enumeration(listOf("application/json")))

        whenever(response.headerNames)
            .thenReturn(listOf("Content-Type"))

        whenever(response.getHeaders("Content-Type"))
            .thenReturn(listOf("application/json"))

        whenever(response.status).thenReturn(200)
        whenever(response.headerNames).thenReturn(listOf("Content-Type"))

        whenever(request.parameterMap).thenReturn(mapOf("a" to listOf("b").toTypedArray()))

        whenever(requestData.requestBody).thenReturn("hello")
        whenever(requestData.responseBody).thenReturn("world")

        val compiledQuery = mock<CompiledQuery>()

        val traceableQuery = TraceableQuery(name = "some-query", compiledQuery = compiledQuery)

        whenever(queryLoader.getQueries())
            .thenReturn(listOf(traceableQuery))

        val intercepted = QueryEvaluator.InterceptedRequestResponse(
            method = "GET",
            statusCode = 200,
            path = "/abc",
            queryParameters = mapOf("a" to listOf("b")),
            requestBody = "hello",
            requestHeaders = mapOf("Accept" to listOf("application/json")),
            responseBody = "world",
            responseHeaders = mapOf("Content-Type" to listOf("application/json")),
            extensions = mapOf("some-extension" to "some-value")
        )

        val result = "some-result"

        whenever(queryEvaluator.evaluate(compiledQuery, intercepted))
            .thenReturn(listOf(result))

        queryReporter.processInterceptedTraffic(request, response)

        verify(resultPublisher).publishResult(traceableQuery, result)
    }

}
