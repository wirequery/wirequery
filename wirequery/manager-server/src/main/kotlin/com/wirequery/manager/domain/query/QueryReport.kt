// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.query

data class QueryReport(
    val appName: String,
    val queryId: String,
    val message: String,
    val startTime: Long,
    val endTime: Long,
    val traceId: String?,
)
