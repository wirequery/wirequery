package com.wirequery.core.query

import com.wirequery.core.query.context.CompiledQuery

class QueryEvaluator(
    private val appHeadEvaluator: AppHeadEvaluator,
    private val streamOperationEvaluator: StreamOperationEvaluator,
    private val aggregatorOperationEvaluator: AggregatorOperationEvaluator,
    private val contextMapCreator: ContextMapCreator
) {

    fun evaluate(compiledQuery: CompiledQuery, intercepted: InterceptedRequestResponse): List<Any> {
        val appHeadEvaluationResult = appHeadEvaluator.evaluate(
            compiledQuery.appHead,
            intercepted.method,
            intercepted.path,
            intercepted.statusCode
        )
        if (!appHeadEvaluationResult.matches) {
            return emptyList()
        }

        val context = contextMapCreator.createMaskedContextMap(intercepted, appHeadEvaluationResult)

        var its = listOf<Any>(context)
        its = evaluateStreamOperations(compiledQuery.streamOperations, its, context)
        if (compiledQuery.aggregatorOperation != null) {
            return evaluateAggregationOperation(compiledQuery, its, context)
        }
        return its
    }

    private fun evaluateStreamOperations(
        streamOperations: List<CompiledQuery.CompiledOperation>,
        previousIts: List<Any>,
        context: Map<String, Any>
    ): List<Any> {
        var nextIts: MutableList<Any>
        var currentIts = previousIts
        streamOperations.forEach { operation ->
            nextIts = mutableListOf()
            currentIts.forEach {
                nextIts.addAll(streamOperationEvaluator.evaluate(operation,
                    mapOf("context" to context, "it" to it)))
            }
            currentIts = nextIts
        }
        return currentIts
    }

    private fun evaluateAggregationOperation(
        compiledQuery: CompiledQuery,
        its: List<Any>,
        context: Map<String, Any>
    ): List<Any> {
        val nextIts = mutableListOf<Any>()
        its.forEach {
            nextIts.addAll(
                aggregatorOperationEvaluator.evaluate(
                    compiledQuery,
                    mapOf("context" to context, "it" to it)
                )
            )
        }
        return nextIts
    }

    data class InterceptedRequestResponse(
        val method: String,
        val statusCode: Int,
        val path: String,
        val queryParameters: Map<String, List<String>>,
        val requestBody: Any?,
        val requestHeaders: Map<String, List<String>>,
        val responseBody: Any?,
        val responseHeaders: Map<String, List<String>>
    )

}
