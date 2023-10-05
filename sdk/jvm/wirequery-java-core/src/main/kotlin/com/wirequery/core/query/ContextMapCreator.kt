package com.wirequery.core.query

import com.wirequery.core.masking.HeadersMasker
import com.wirequery.core.masking.ObjectMasker

class ContextMapCreator(
    private val headersMasker: HeadersMasker,
    private val objectMasker: ObjectMasker
) {

    fun createContextMap(
        intercepted: QueryEvaluator.InterceptedRequestResponse,
        appHeadEvaluationResult: AppHeadEvaluator.AppHeadEvaluationResult
    ) = mapOf(
        "method" to intercepted.method,
        "path" to intercepted.path,
        "pathVariables" to appHeadEvaluationResult.pathVariables,
        "statusCode" to intercepted.statusCode,
        "queryParameters" to intercepted.queryParameters,
        "requestBody" to intercepted.requestBody,
        "responseBody" to intercepted.responseBody,
        "requestHeaders" to intercepted.requestHeaders,
        "responseHeaders" to intercepted.responseHeaders,
        "extensions" to intercepted.extensions,
        "startTime" to intercepted.startTime,
        "endTime" to intercepted.endTime,
        "traceId" to intercepted.traceId,
    )
        .filter { it.value != null }
        .map { it.key to it.value!! }
        .toMap()

    fun createMaskedContextMap(
        intercepted: QueryEvaluator.InterceptedRequestResponse,
        appHeadEvaluationResult: AppHeadEvaluator.AppHeadEvaluationResult
    ) = mapOf(
        "method" to intercepted.method,
        "path" to intercepted.path,
        "pathVariables" to appHeadEvaluationResult.pathVariables,
        "statusCode" to intercepted.statusCode,
        "queryParameters" to intercepted.queryParameters,
        "requestBody" to intercepted.requestBody?.let(objectMasker::mask),
        "responseBody" to intercepted.responseBody?.let(objectMasker::mask),
        "requestHeaders" to headersMasker.maskRequestHeaders(intercepted.requestHeaders),
        "responseHeaders" to headersMasker.maskResponseHeaders(intercepted.responseHeaders),
        "extensions" to intercepted.extensions,
        "startTime" to intercepted.startTime,
        "endTime" to intercepted.endTime,
        "traceId" to intercepted.traceId,
    )
        .filter { it.value != null }
        .map { it.key to it.value!! }
        .toMap()

}
