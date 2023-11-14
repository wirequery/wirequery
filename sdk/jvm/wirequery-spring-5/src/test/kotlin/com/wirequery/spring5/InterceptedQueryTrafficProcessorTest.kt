// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring5

import com.fasterxml.jackson.databind.JsonNode
import com.wirequery.core.query.QueryEvaluator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Clock
import java.time.Instant.ofEpochMilli
import java.time.ZoneId
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class InterceptedQueryTrafficProcessorTest {

    @Mock
    private lateinit var requestData: RequestData

    @Mock
    private lateinit var asyncQueriesProcessor: AsyncQueriesProcessor

    @Mock
    private lateinit var traceCache: TraceCache

    @Mock
    private lateinit var traceProvider: TraceProvider

    @InjectMocks
    private lateinit var interceptedQueryTrafficProcessor: InterceptedQueryTrafficProcessor

    @BeforeEach
    fun init() {
        interceptedQueryTrafficProcessor.clock = Clock.fixed(ofEpochMilli(20), ZoneId.systemDefault());
    }

    @Test
    fun `intercepted traffic is mapped to the WireQuery domain, evaluated and sent to the AsyncQueriesProcessor`() {
        val request = mock<ContentCachingRequestWrapper>()
        val response = mock<ContentCachingResponseWrapper>()

        whenever(traceProvider.traceId())
            .thenReturn("abc")

        whenever(request.method).thenReturn("GET")
        whenever(request.requestURI).thenReturn("/abc")

        whenever(request.headerNames)
            .thenReturn(Collections.enumeration(listOf("Accept", "traceparent")))

        doReturn(Collections.enumeration(listOf("application/json")))
            .whenever(request).getHeaders("Accept")

        doReturn(Collections.enumeration(listOf("00-abc-def")))
            .whenever(request).getHeaders("traceparent")

        whenever(response.headerNames)
            .thenReturn(listOf("Content-Type"))

        whenever(response.getHeaders("Content-Type"))
            .thenReturn(listOf("application/json"))

        whenever(response.status).thenReturn(200)
        whenever(response.headerNames).thenReturn(listOf("Content-Type"))

        whenever(request.parameterMap).thenReturn(mapOf("a" to listOf("b").toTypedArray()))

        whenever(requestData.requestBody).thenReturn("hello")
        whenever(requestData.responseBody).thenReturn("world")
        whenever(requestData.startTime).thenReturn(10)

        val someValueNode = mock<JsonNode>()
        whenever(requestData.extensions).thenReturn(mapOf("some-extension" to someValueNode))

        val intercepted = QueryEvaluator.InterceptedRequestResponse(
            method = "GET",
            statusCode = 200,
            path = "/abc",
            queryParameters = mapOf("a" to listOf("b")),
            requestBody = "hello",
            requestHeaders = mapOf("Accept" to listOf("application/json"), "traceparent" to listOf("00-abc-def")),
            responseBody = "world",
            responseHeaders = mapOf("Content-Type" to listOf("application/json")),
            extensions = mapOf("some-extension" to someValueNode),
            startTime = 10,
            endTime = 20,
            traceId = "abc"
        )

        interceptedQueryTrafficProcessor.processInterceptedTraffic(request, response)

        verify(traceCache).store(intercepted)
        verify(asyncQueriesProcessor).execute(intercepted)
    }

}
