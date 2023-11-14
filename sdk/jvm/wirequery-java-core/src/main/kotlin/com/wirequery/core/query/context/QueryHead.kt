// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query.context

data class QueryHead(
    val method: String,
    val path: String,
    val statusCode: String,
)
