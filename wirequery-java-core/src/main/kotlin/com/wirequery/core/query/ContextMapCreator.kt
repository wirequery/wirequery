package com.wirequery.core.query

class ContextMapCreator(
    private val headersMasker: com.wirequery.core.masking.HeadersMasker,
    private val objectMasker: com.wirequery.core.masking.ObjectMasker
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
        "extensions" to intercepted.extensions
    )
        .filter { it.value != null }
        .map { it.key to it.value!! }
        .toMap()

}
