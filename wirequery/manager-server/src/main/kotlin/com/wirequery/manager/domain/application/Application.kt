package com.wirequery.manager.domain.application

import java.time.OffsetDateTime

data class Application(
    val id: Int,
    val name: String,
    val description: String,
    val apiKey: String,
    val inQuarantine: Boolean,
    val quarantineRule: String?,
    val quarantineReason: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)
