package com.wirequery.spring6

import com.fasterxml.jackson.databind.JsonNode
import com.wirequery.core.query.QueryEvaluator
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
internal class InterceptedQueryTrafficProcessorTest {

    @Mock
    private lateinit var requestData: RequestData

    @Mock
    private lateinit var asyncQueriesProcessor: AsyncQueriesProcessor

    @InjectMocks
    private lateinit var interceptedQueryTrafficProcessor: InterceptedQueryTrafficProcessor

    @Test
    fun `intercepted traffic is mapped to the WireQuery domain, evaluated and sent to the AsyncQueriesProcessor`() {
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

        val someValueNode = mock<JsonNode>()
        whenever(requestData.extensions).thenReturn(mapOf("some-extension" to someValueNode))

        val intercepted = QueryEvaluator.InterceptedRequestResponse(
            method = "GET",
            statusCode = 200,
            path = "/abc",
            queryParameters = mapOf("a" to listOf("b")),
            requestBody = "hello",
            requestHeaders = mapOf("Accept" to listOf("application/json")),
            responseBody = "world",
            responseHeaders = mapOf("Content-Type" to listOf("application/json")),
            extensions = mapOf("some-extension" to someValueNode)
        )

        interceptedQueryTrafficProcessor.processInterceptedTraffic(request, response)

        verify(asyncQueriesProcessor).execute(intercepted)
    }

}