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
