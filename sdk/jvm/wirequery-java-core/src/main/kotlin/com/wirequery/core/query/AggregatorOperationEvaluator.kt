// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query

import com.wirequery.core.query.context.CompiledQuery

class AggregatorOperationEvaluator {
    fun evaluate(
        compiledQuery: CompiledQuery,
        context: Map<String, Any>,
    ): List<Any> {
        if (compiledQuery.aggregatorOperation == null) {
            return context["it"]
                ?.let(::listOf)
                ?: emptyList()
        }
        return when (compiledQuery.aggregatorOperation.name) {
            "distinct" ->
                handleDistinct(compiledQuery, context["it"])
            else ->
                error("Unknown operation: ${compiledQuery.aggregatorOperation.name}")
        }
    }

    private fun handleDistinct(
        compiledQuery: CompiledQuery,
        previousResult: Any?,
    ): List<Any> {
        if (compiledQuery.aggregatorOperation?.celExpression != null) {
            error("Cel expression not allowed for 'distinct'")
        }
        if (compiledQuery.aggregatorMemory == null) {
            compiledQuery.aggregatorMemory = DistinctAggregatorMemory()
        }
        val mem = compiledQuery.aggregatorMemory as DistinctAggregatorMemory

        // TODO replace with proper hashing function
        val hash = previousResult.hashCode()
        if (hash in mem.previouslyOccurredHashCodes) {
            return emptyList()
        }
        mem.previouslyOccurredHashCodes += hash
        return previousResult?.let(::listOf) ?: listOf()
    }

    data class DistinctAggregatorMemory(
        val previouslyOccurredHashCodes: MutableSet<Int> = mutableSetOf(),
    ) : CompiledQuery.AggregatorMemory
}
