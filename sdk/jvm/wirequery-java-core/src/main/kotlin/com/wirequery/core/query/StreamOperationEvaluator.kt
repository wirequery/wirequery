// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query

import com.fasterxml.jackson.databind.node.ArrayNode
import com.wirequery.core.query.context.CompiledQuery

class StreamOperationEvaluator {
    fun evaluate(
        compiledOperation: CompiledQuery.CompiledOperation,
        context: Map<String, Any>,
    ): List<Any> {
        val celExpression =
            compiledOperation.celExpression
                ?: error("No expression provided")
        val expressionResult = celExpression.eval(context)
        return when (compiledOperation.name) {
            "map" -> evaluateMap(expressionResult)
            "flatMap" -> evaluateFlatMap(expressionResult)
            "filter" -> evaluateFilter(expressionResult, context)
            else -> error("Unknown operation")
        }
    }

    private fun evaluateMap(result: Any): List<Any> {
        return listOf(result)
    }

    private fun evaluateFlatMap(result: Any): List<Any> {
        if (result is Iterable<*>) {
            return result.toList() as List<Any>
        }
        if (result is Array<*>) {
            return result.toList() as List<Any>
        }
        if (result is ArrayNode) {
            return result.toList()
        }
        error("Unable to flatten flatMap result")
    }

    private fun evaluateFilter(
        result: Any,
        context: Map<String, Any>,
    ): List<Any> {
        if (result == true) {
            return listOfNotNull(context["it"])
        } else if (result == false) {
            return emptyList()
        }
        error("Return value of filter expression is not a boolean")
    }
}
