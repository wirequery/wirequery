package com.wirequery.manager.domain.template

import java.time.OffsetDateTime

data class Template(
    val id: Int,
    val name: String,
    val description: String,
    val fields: List<Field>,
    val nameTemplate: String,
    val descriptionTemplate: String,
    val allowUserInitiation: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
) {
    data class Field(
        val key: String,
        val label: String,
        val type: FieldType,
    )

    enum class FieldType {
        TEXT,
        TEXTAREA,
        INTEGER,
        FLOAT,
        BOOLEAN,
    }
}
