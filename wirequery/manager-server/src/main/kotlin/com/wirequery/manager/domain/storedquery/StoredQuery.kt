package com.wirequery.manager.domain.storedquery

import java.time.OffsetDateTime

data class StoredQuery(
    val id: Int,
    val sessionId: Int?,
    val applicationId: Int,
    val name: String,
    val type: String,
    val query: String,
    val queryLimit: Int,
    val endDate: OffsetDateTime?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)
