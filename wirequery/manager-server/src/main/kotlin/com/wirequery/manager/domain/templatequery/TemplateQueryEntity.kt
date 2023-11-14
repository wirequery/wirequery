// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.templatequery

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("template_querys")
data class TemplateQueryEntity(
    @Id
    val id: Int? = null,
    val templateId: Int,
    val applicationId: Int,
    val nameTemplate: String,
    val queryTemplate: String,
    val queryLimit: Int,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @CreatedBy
    val createdBy: String? = null,
    @LastModifiedBy
    val updatedBy: String? = null,
)
