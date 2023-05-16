package com.wirequery.core.query

import com.wirequery.core.query.context.CompiledQuery
import com.wirequery.core.query.context.Query

class QueryCompiler(
    private val expressionCompiler: ExpressionCompiler,
    private val queryAuthorizer: QueryAuthorizer
) {

    fun compile(query: Query): CompiledQuery {
        // It may look a bit odd to have the "query authorizer" be part of the compiler.
        // However, if you see it as a "compile-time error", I guess it's okay-ish...
        if (!queryAuthorizer.isAuthorized(query.queryHead.method, query.queryHead.path)) {
            error("Query not authorized for compilation.")
        }
        return CompiledQuery(
            queryHead = query.queryHead,
            streamOperations = query.streamOperations.map(::compileOperation),
            aggregatorOperation = query.aggregatorOperation?.let(::compileOperation)
        )
    }

    private fun compileOperation(operation: Query.Operation) = CompiledQuery.CompiledOperation(
        name = operation.name,
        celExpression = operation.celExpression?.let(expressionCompiler::compile)
    )
}
