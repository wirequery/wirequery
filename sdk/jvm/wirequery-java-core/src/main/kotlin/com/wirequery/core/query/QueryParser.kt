package com.wirequery.core.query

import com.wirequery.core.query.context.Query.Operation
import com.wirequery.core.query.context.QueryHead
import com.wirequery.core.query.context.Query

class QueryParser {

    fun parse(expression: String): Query {
        val streamParts = splitExpressionIntoStreamParts(expression).toList()
        val appHead = createAppHead(streamParts.first())
        val operations = streamParts.drop(1).mapNotNull { streamPartToOperation(it) }.toList()
        if (isAggregatorOperation(operations.lastOrNull())) {
            return createQueryWithAggregatorOperation(appHead, operations)
        }
        return createQueryWithoutAggregatorOperation(appHead, operations)
    }

    private fun splitExpressionIntoStreamParts(expression: String) =
        QUERY_PARTS_PATTERN.findAll(expression).map { it.value }

    private fun createAppHead(value: String): QueryHead {
        var method = ""
        var path = ""
        var statusCode = ""
        value.split(" ").forEach { part ->
            when {
                part in METHODS ->
                    errorIfNonBlankString("Method", method).also { method = part }
                part.startsWith("/") ->
                    errorIfNonBlankString("Path", path).also { path = part }
                part.matches("[0-9x]{3}".toRegex()) ->
                    errorIfNonBlankString("Status code", statusCode).also { statusCode = part }
            }
        }
        return QueryHead(
            method = method,
            path = path,
            statusCode = statusCode,
        )
    }

    private fun errorIfNonBlankString(name: String, checkAlreadySet: String) {
        if (checkAlreadySet.isNotBlank()) {
            error("$name is already set")
        }
    }

    private fun streamPartToOperation(value: String): Operation? {
        if (value.isEmpty()) {
            return null
        }
        return value
            .trim()
            .split(" ", limit = 2)
            .let { Operation(it[0], it.getOrNull(1)) }
    }

    private fun createQueryWithAggregatorOperation(queryHead: QueryHead, operations: List<Operation>): Query {
        val streamOperations = operations.dropLast(1)
        streamOperations.forEach { check(isStreamOperation(it)) }
        return Query(
            queryHead = queryHead,
            streamOperations = streamOperations,
            aggregatorOperation = operations.last()
        )
    }

    private fun createQueryWithoutAggregatorOperation(queryHead: QueryHead, operations: List<Operation>): Query {
        operations.forEach { if (!isStreamOperation(it)) error("${it.name} is not a known stream operation") }
        return Query(
            queryHead = queryHead,
            streamOperations = operations,
            aggregatorOperation = null
        )
    }

    private fun isAggregatorOperation(operation: Operation?): Boolean {
        return operation != null && operation.name in AGGREGATOR_OPERATIONS
    }

    private fun isStreamOperation(operation: Operation?): Boolean {
        return operation != null && operation.name in STREAM_OPERATIONS
    }

    private companion object {
        /**
         * This regex splits by "|", while taking into account character escapes, the ||-operator
         * and substrings starting with ' and ".
         */
        val QUERY_PARTS_PATTERN = "(([^'\"|](\\|\\|)?)*('([^'\\\\]|[\\\\].)*'|\"([^\"\\\\]|[\\\\].)*\")*)*".toRegex()

        val METHODS = listOf("GET", "POST", "PUT", "HEAD", "DELETE", "PATCH", "OPTIONS", "CONNECT", "TRACE")

        val STREAM_OPERATIONS = listOf(
            "map", "flatMap", "filter"
        )

        // In a future release, the following operations must be supported:
        // "count", "min", "max", "sum", "distinct",
        // "countBy", "minBy", "maxBy", "sumBy", "distinctBy",
        // "countDistinct", "countDistinctBy",
        val AGGREGATOR_OPERATIONS = listOf("distinct")
    }
}
