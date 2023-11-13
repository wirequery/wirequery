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
    val main: Boolean,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
)
