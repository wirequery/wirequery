// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.templatequery

import java.time.OffsetDateTime

data class TemplateQuery(
    val id: Int,
    val templateId: Int,
    val applicationId: Int,
    val nameTemplate: String,
    val type: Type,
    val queryTemplate: String,
    val queryLimit: Int,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
) {
    enum class Type {
        QUERY,
        QUERY_WITH_TRACING,
    }
}
