package com.wirequery.manager.domain.role

import java.time.OffsetDateTime

data class Role(
    val id: Int,
    val name: String,
    val authorisationNames: List<String>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)
