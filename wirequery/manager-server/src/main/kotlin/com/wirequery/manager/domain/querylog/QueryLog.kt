// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import java.time.OffsetDateTime

data class QueryLog(
    val storedQueryId: Int,
    val message: String,
    val startTime: Long,
    val endTime: Long,
    val appName: String,
    val traceId: String?,
    val createdAt: OffsetDateTime,
)
