// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.statistic.StatisticFixtures.INCREMENT_STATISTIC_FIXTURE_1
import com.wirequery.manager.domain.statistic.StatisticFixtures.SET_STATISTIC_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class StatisticIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var statisticService: StatisticService

    @Test
    fun `Statistics can be set, incremented and fetched`() {
        statisticService.set(SET_STATISTIC_FIXTURE_1)
        statisticService.increment(INCREMENT_STATISTIC_FIXTURE_1)

        assertThat(statisticService.findAll()).isNotEmpty
    }
}
