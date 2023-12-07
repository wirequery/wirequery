// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import java.time.LocalDate

data class Statistic(
    val id: Long,
    val moment: LocalDate,
    val hour: Int,
    val type: TypeEnum,
    val metadata: String,
    val amount: Int,
) {
    enum class TypeEnum {
        INSTANT_QUERY,
        INSTANT_QUERY_LOG,
        INSTANT_QUERY_LOG_CHUNKS,
        QUERY_LOG,
        QUERY_LOG_CHUNKS,
        LOGIN,
        RECORDING,
    }
}
