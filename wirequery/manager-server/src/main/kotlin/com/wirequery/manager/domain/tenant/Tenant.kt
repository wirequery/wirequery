package com.wirequery.manager.domain.tenant

import java.time.OffsetDateTime

data class Tenant(
    val id: Int,
    val name: String,
    val slug: String,
    val plan: String,
    val enabled: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
)
