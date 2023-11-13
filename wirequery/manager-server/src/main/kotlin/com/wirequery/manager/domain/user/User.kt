package com.wirequery.manager.domain.user

import java.time.OffsetDateTime

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val enabled: Boolean,
    val roles: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)
