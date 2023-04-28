package com.wirequery.core.query

import com.wirequery.core.query.context.CompiledQuery
import com.wirequery.core.query.context.Query

class QueryCompiler(
    private val expressionCompiler: ExpressionCompiler
) {

    fun compile(query: Query): CompiledQuery {
        return CompiledQuery(
            appHead = query.appHead,
            streamOperations = query.streamOperations.map(::compileOperation),
            aggregatorOperation = query.aggregatorOperation?.let(::compileOperation)
        )
    }

    private fun compileOperation(operation: Query.Operation) = CompiledQuery.CompiledOperation(
        name = operation.name,
        celExpression = operation.celExpression?.let(expressionCompiler::compile)
    )
}
