package com.wirequery.manager.domain.session

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("sessions")
data class SessionEntity(
    @Id
    val id: Int? = null,
    val name: String,
    val description: String,
    val draft: Boolean,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @CreatedBy
    val createdBy: String? = null,
    @LastModifiedBy
    val updatedBy: String? = null,
)
