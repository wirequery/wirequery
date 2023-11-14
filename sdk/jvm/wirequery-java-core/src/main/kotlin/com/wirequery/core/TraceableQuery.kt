// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core

import com.wirequery.core.query.context.CompiledQuery

data class TraceableQuery(
    val queryId: String,
    val compiledQuery: CompiledQuery
)
