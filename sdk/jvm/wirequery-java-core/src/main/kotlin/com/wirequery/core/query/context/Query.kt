// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query.context

data class Query(
    val queryHead: QueryHead,
    val streamOperations: List<Operation>,
    val aggregatorOperation: Operation?,
) {
    data class Operation(
        val name: String,
        val celExpression: String?,
    )
}
