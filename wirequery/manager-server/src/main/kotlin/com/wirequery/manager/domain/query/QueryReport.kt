package com.wirequery.manager.domain.query

data class QueryReport(
    val appName: String,
    val queryId: String,
    val message: String,
    val startTime: Long,
    val endTime: Long,
    val traceId: String?,
)
