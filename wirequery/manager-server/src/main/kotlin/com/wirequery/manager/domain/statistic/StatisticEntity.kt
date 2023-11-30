// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import com.wirequery.manager.domain.statistic.Statistic.TypeEnum
import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("statistics")
data class StatisticEntity(
    @Id
    val id: Long? = null,
    val moment: LocalDate,
    val hour: Int,
    val type: TypeEnum,
    val metadata: String,
    val amount: Int,
)
