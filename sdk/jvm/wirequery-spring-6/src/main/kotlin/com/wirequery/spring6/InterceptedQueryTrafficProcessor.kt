// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring6

import com.wirequery.core.query.QueryEvaluator
import org.springframework.stereotype.Service
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Clock

@Service
class InterceptedQueryTrafficProcessor(
    private val requestData: RequestData,
    private val asyncQueriesProcessor: AsyncQueriesProcessor,
    private val traceCache: TraceCache,
    private val traceProvider: TraceProvider
) {

    internal var clock = Clock.systemDefaultZone()

    fun processInterceptedTraffic(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper
    ) {
        val intercepted = QueryEvaluator.InterceptedRequestResponse(
            method = request.method,
            statusCode = response.status,
            path = request.requestURI,
            queryParameters = request.parameterMap.map { it.key to it.value.toList() }.toMap(),
            requestBody = requestData.requestBody,
            requestHeaders = extractRequestHeaders(request),
            responseBody = requestData.responseBody,
            responseHeaders = extractResponseHeaders(response),
            extensions = requestData.extensions,
            startTime = requestData.startTime,
            endTime = clock.millis(),
            traceId = traceProvider.traceId()
        )
        traceCache.store(intercepted)
        asyncQueriesProcessor.execute(intercepted)
    }

    private fun extractRequestHeaders(request: ContentCachingRequestWrapper): Map<String, List<String>> {
        val headers = mutableMapOf<String, List<String>>()
        val iter = request.headerNames.asIterator()
        while (iter.hasNext()) {
            val headerName = iter.next()
            headers[headerName] = request.getHeaders(headerName).toList()
        }
        return headers
    }

    private fun extractResponseHeaders(response: ContentCachingResponseWrapper): Map<String, List<String>> {
        val headers = mutableMapOf<String, List<String>>()
        for (headerName in response.headerNames) {
            headers[headerName] = response.getHeaders(headerName).toList()
        }
        return headers
    }
}
