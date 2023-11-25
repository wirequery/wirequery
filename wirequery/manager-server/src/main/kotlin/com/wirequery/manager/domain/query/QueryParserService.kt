// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.query

import com.wirequery.manager.domain.FunctionalException.Companion.functionalError
import org.springframework.stereotype.Service

@Service
class QueryParserService {
    fun parse(expression: String): Query {
        val operations = mutableListOf<Operation>()
        lateinit var query: Query
        QUERY_PARTS_PATTERN
            .findAll(expression)
            .filter { it.value.trim() != "" }
            .map { it.value }
            .forEachIndexed { i, value ->
                when (i) {
                    0 -> query = withQueryHead(value.trim())
                    else ->
                        operations +=
                            value.trim().split(" ", limit = 2).let {
                                Operation(
                                    name = it[0],
                                    celExpression = if (it.size == 1) null else it[1],
                                )
                            }
                }
            }

        if (isAggregatorOperation(operations.lastOrNull())) {
            return query.copy(
                streamOperations = operations.dropLast(1),
                aggregatorOperation = operations.last(),
            )
        }
        return query.copy(
            streamOperations = operations,
            aggregatorOperation = null,
        )
    }

    private fun isAggregatorOperation(operation: Operation?): Boolean {
        return operation != null && operation.name in AGGREGATOR_FUNCTIONS
    }

    private fun withQueryHead(value: String): Query {
        var appName = ""
        var method = ""
        var path = ""
        var statusCode = ""

        value.split(" ").forEachIndexed { i, part ->
            when {
                i == 0 ->
                    appName = part

                part in METHODS ->
                    method = part

                part.startsWith("/") ->
                    if (path != "") {
                        functionalError("Path already set in query.")
                    } else {
                        path = part
                    }

                part.matches("[0-9x]{3}".toRegex()) ->
                    if (statusCode != "") {
                        functionalError("Status code already set in query.")
                    } else {
                        statusCode = part
                    }

                else ->
                    functionalError("Unknown parameter type.")
            }
        }
        return Query(
            queryHead =
                QueryHead(
                    appName = appName,
                    method = method,
                    path = path,
                    statusCode = statusCode,
                ),
            streamOperations = emptyList(),
            aggregatorOperation = null,
        )
    }

    data class Query(
        val queryHead: QueryHead,
        val streamOperations: List<Operation>,
        val aggregatorOperation: Operation?,
    )

    data class QueryHead(
        val appName: String,
        val method: String,
        val path: String,
        val statusCode: String,
    )

    data class Operation(
        val name: String,
        val celExpression: String?,
    )

    private companion object {
        /**
         * This regex splits by "|", while taking into account character escapes, the ||-operator
         * and substrings starting with ' and ".
         */
        val QUERY_PARTS_PATTERN = "(([^'\"|](\\|\\|)?)*('([^'\\\\]|[\\\\].)*'|\"([^\"\\\\]|[\\\\].)*\")*)*".toRegex()

        val METHODS = listOf("GET", "POST", "PUT", "HEAD", "DELETE", "PATCH", "OPTIONS", "CONNECT", "TRACE")

        val AGGREGATOR_FUNCTIONS =
            listOf(
                "count", "min", "max", "sum", "distinct",
                "countBy", "minBy", "maxBy", "sumBy", "distinctBy",
                "countDistinct", "countDistinctBy",
            )
    }
}
