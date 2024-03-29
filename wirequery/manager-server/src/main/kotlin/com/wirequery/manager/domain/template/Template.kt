// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

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
    val apiKey: String,
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
