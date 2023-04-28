package com.wirequery.core.query.context

import org.projectnessie.cel.tools.Script

data class CompiledQuery(
    val appHead: AppHead,
    val streamOperations: List<CompiledOperation>,
    val aggregatorOperation: CompiledOperation?
) {

    data class CompiledOperation(
        val name: String,
        val celExpression: Script?
    )

    /** Memory to be used in the compiled query's entire lifespan for aggregation. */
    var aggregatorMemory: AggregatorMemory? = null

    interface AggregatorMemory

}

