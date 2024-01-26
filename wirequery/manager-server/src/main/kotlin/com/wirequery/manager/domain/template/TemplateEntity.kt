// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.template

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("templates")
data class TemplateEntity(
    @Id
    val id: Int? = null,
    val name: String,
    val description: String,
    @MappedCollection(idColumn = "template_id", keyColumn = "order_key")
    val fields: List<FieldEntity>,
    val nameTemplate: String,
    val descriptionTemplate: String,
    val allowUserInitiation: Boolean,
    val apiKey: String,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @CreatedBy
    val createdBy: String? = null,
    @LastModifiedBy
    val updatedBy: String? = null,
) {
    @Table("template_fields")
    data class FieldEntity(
        val key: String,
        val label: String,
        val type: Template.FieldType,
    )
}
