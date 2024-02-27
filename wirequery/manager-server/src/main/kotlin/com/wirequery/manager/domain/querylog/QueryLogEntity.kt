// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("query_logs")
data class QueryLogEntity(
    val storedQueryId: Int,
    val message: String,
    val startTime: Long,
    val endTime: Long,
    val appName: String,
    val traceId: String?,
    val requestCorrelationId: String?,
    val main: Boolean,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
)
