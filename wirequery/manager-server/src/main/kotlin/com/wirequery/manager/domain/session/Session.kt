package com.wirequery.manager.domain.session

import java.time.OffsetDateTime

data class Session(
    val id: Int,
    val name: String,
    val description: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val draft: Boolean,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)
