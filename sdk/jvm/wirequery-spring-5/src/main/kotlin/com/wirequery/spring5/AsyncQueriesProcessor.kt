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
            queryEvaluator.evaluate(query.compiledQuery, intercepted).forEach { result ->
                resultPublisher.publishResult(query, result)
            }
        }
    }
}
