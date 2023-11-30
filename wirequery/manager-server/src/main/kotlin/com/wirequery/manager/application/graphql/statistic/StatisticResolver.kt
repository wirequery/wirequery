// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.statistic

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.wirequery.manager.domain.statistic.Statistic
import com.wirequery.manager.domain.statistic.StatisticService

@DgsComponent
class StatisticResolver(
    private val statisticService: StatisticService,
) {
    @DgsQuery
    fun statistics(): Iterable<Statistic> {
        return statisticService.findAll()
    }
}
