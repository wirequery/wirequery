package com.wirequery.manager.domain.tenant

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tenants")
data class TenantEntity(
    @Id
    val id: Int? = null,
    val name: String,
    val slug: String,
    val plan: String,
    val enabled: Boolean,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
)
