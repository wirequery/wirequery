// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

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
        "took" to intercepted.endTime - intercepted.startTime,
        "traceId" to intercepted.traceId,
    )
        .filter { it.value != null }
        .map { it.key to it.value!! }
        .toMap()

    fun createMaskedContextMap(
        intercepted: QueryEvaluator.InterceptedRequestResponse,
        appHeadEvaluationResult: AppHeadEvaluator.AppHeadEvaluationResult? = null
    ) = mapOf(
        "method" to intercepted.method,
        "path" to intercepted.path,
        "pathVariables" to appHeadEvaluationResult?.pathVariables,
        "statusCode" to intercepted.statusCode,
        "queryParameters" to intercepted.queryParameters,
        "requestBody" to intercepted.requestBody?.let(objectMasker::mask),
        "responseBody" to intercepted.responseBody?.let(objectMasker::mask),
        "requestHeaders" to headersMasker.maskRequestHeaders(intercepted.requestHeaders),
        "responseHeaders" to headersMasker.maskResponseHeaders(intercepted.responseHeaders),
        "extensions" to intercepted.extensions,
        "took" to intercepted.endTime - intercepted.startTime,
        "traceId" to intercepted.traceId,
    )
        .filter { it.value != null }
        .map { it.key to it.value!! }
        .toMap()

}
