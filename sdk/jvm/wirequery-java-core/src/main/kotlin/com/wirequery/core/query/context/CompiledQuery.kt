// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query.context

import org.projectnessie.cel.tools.Script

data class CompiledQuery(
    val queryHead: QueryHead,
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

