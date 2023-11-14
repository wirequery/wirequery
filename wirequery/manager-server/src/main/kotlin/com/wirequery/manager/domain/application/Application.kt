// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.application

import java.time.OffsetDateTime

data class Application(
    val id: Int,
    val name: String,
    val description: String,
    val apiKey: String,
    val inQuarantine: Boolean,
    val quarantineRule: String?,
    val quarantineReason: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
)
