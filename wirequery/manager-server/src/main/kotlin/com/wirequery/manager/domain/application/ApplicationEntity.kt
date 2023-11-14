package com.wirequery.manager.domain.application

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("applications")
data class ApplicationEntity(
    @Id
    val id: Int? = null,
    val name: String,
    val description: String,
    val apiKey: String,
    val inQuarantine: Boolean,
    val quarantineRule: String?,
    val quarantineReason: String?,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @CreatedBy
    val createdBy: String? = null,
    @LastModifiedBy
    val updatedBy: String? = null,
)