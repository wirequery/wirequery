package com.wirequery.manager.domain.templatequery

import java.time.OffsetDateTime

data class TemplateQuery(
    val id: Int,
    val templateId: Int,
    val applicationId: Int,
    val nameTemplate: String,
    val queryTemplate: String,
    val queryLimit: Int,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)
