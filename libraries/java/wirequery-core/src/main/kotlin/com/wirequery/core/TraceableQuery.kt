package com.wirequery.core

import com.wirequery.core.query.context.CompiledQuery

data class TraceableQuery(
    val name: String,
    val compiledQuery: CompiledQuery
)
