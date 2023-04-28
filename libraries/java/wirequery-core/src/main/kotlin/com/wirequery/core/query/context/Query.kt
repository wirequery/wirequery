package com.wirequery.core.query.context

data class Query(
    val appHead: AppHead,
    val streamOperations: List<Operation>,
    val aggregatorOperation: Operation?
) {
    data class Operation(
        val name: String,
        val celExpression: String?,
    )
}
