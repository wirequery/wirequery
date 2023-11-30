// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import java.time.LocalDate

object StatisticFixtures {
    val STATISTIC_FIXTURE_WITH_ID_1 =
        Statistic(
            id = 1,
            moment = LocalDate.of(2000, 1, 1),
            hour = 1,
            type = Statistic.TypeEnum.QUERY_LOG,
            metadata = "{\"a\":\"b\"}",
            amount = 1,
        )

    val STATISTIC_ENTITY_FIXTURE_1 =
        StatisticEntity(
            id = null,
            moment = LocalDate.of(2000, 1, 1),
            hour = 1,
            type = Statistic.TypeEnum.QUERY_LOG,
            metadata = "{\"a\":\"b\"}",
            amount = 1,
        )

    val SET_STATISTIC_FIXTURE_1 =
        StatisticService.SetStatisticInput(
            type = Statistic.TypeEnum.QUERY_LOG,
            metadata = mapOf("a" to "b"),
            amount = 1,
        )

    val INCREMENT_STATISTIC_FIXTURE_1 =
        StatisticService.IncrementStatisticInput(
            type = Statistic.TypeEnum.QUERY_LOG,
            metadata = mapOf("a" to "b"),
            amount = 1,
        )

    val STATISTIC_ENTITY_FIXTURE_WITH_ID_1 =
        STATISTIC_ENTITY_FIXTURE_1.copy(
            id = 1,
        )
}
