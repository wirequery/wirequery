// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring5

import com.wirequery.core.QueryLoader
import com.wirequery.core.ResultPublisher
import com.wirequery.core.query.QueryEvaluator
import com.wirequery.core.query.QueryEvaluator.InterceptedRequestResponse
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AsyncQueriesProcessor(
    private val queryEvaluator: QueryEvaluator,
    private val queryLoader: QueryLoader,
    private val resultPublisher: ResultPublisher,
) {
    @Async
    fun execute(intercepted: InterceptedRequestResponse) {
        queryLoader.getQueries().forEach { query ->
            try {
                queryEvaluator.evaluate(query.compiledQuery, intercepted).forEach { result ->
                    resultPublisher.publishResult(
                        query,
                        result,
                        intercepted.startTime,
                        intercepted.endTime,
                        intercepted.traceId,
                        intercepted.requestCorrelationId,
                    )
                }
            } catch (e: Exception) {
                resultPublisher.publishError(
                    query.queryId,
                    "" + e.message,
                    intercepted.startTime,
                    intercepted.endTime,
                    intercepted.traceId,
                    intercepted.requestCorrelationId,
                )
            }
        }
    }
}
