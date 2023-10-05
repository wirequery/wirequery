package com.wirequery.spring5

import com.wirequery.core.query.QueryEvaluator
import org.springframework.stereotype.Service
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Clock

@Service
class InterceptedQueryTrafficProcessor(
    private val requestData: RequestData,
    private val asyncQueriesProcessor: AsyncQueriesProcessor
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
            traceId = extractTraceId(request)
        )
        asyncQueriesProcessor.execute(intercepted)
    }

    private fun extractTraceId(request: ContentCachingRequestWrapper): String? {
        return request.getHeader("traceparent")
            ?.split("-")
            ?.let {
                if (it.size != 3) {
                    return null
                }
                if (it[0] != "00") {
                    return null
                }
                return it[1]
            }
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
