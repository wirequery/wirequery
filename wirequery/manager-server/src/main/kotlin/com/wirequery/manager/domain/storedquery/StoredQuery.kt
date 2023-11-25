// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.storedquery

import java.time.OffsetDateTime

data class StoredQuery(
    val id: Int,
    val sessionId: Int?,
    val applicationId: Int,
    val name: String,
    val type: Type,
    val query: String,
    val queryLimit: Int,
    val endDate: OffsetDateTime?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
) {
    enum class Type {
        QUERY,
        QUERY_WITH_TRACING
    }
}
