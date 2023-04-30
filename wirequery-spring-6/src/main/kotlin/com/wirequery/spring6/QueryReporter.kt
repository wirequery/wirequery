package com.wirequery.spring6

import com.wirequery.core.QueryLoader
import com.wirequery.core.ResultPublisher
import com.wirequery.core.query.QueryEvaluator
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class QueryReporter(
    private val queryEvaluator: QueryEvaluator,
    private val queryLoader: QueryLoader,
    private val resultPublisher: ResultPublisher,
    private val requestData: RequestData
) {

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
            extensions = requestData.extensions
        )

        queryLoader.getQueries().forEach { query ->
            queryEvaluator.evaluate(query.compiledQuery, intercepted).forEach { result ->
                resultPublisher.publishResult(query, result)
            }
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
