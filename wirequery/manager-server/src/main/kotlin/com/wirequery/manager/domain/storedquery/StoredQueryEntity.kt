package com.wirequery.manager.domain.storedquery

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("stored_querys")
data class StoredQueryEntity(
    @Id
    val id: Int? = null,
    val sessionId: Int?,
    val applicationId: Int,
    val name: String,
    val type: String,
    val query: String,
    val queryLimit: Int,
    val disabled: Boolean,
    val endDate: LocalDateTime?,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @CreatedBy
    val createdBy: String? = null,
    @LastModifiedBy
    val updatedBy: String? = null,
)
