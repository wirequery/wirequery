package com.wirequery.core

import com.wirequery.core.query.context.CompiledQuery

data class TraceableQuery(
    val queryId: String,
    val compiledQuery: CompiledQuery
)
