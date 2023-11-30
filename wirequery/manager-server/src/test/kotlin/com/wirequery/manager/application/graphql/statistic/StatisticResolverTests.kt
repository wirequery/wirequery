// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.statistic

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.wirequery.manager.domain.statistic.StatisticFixtures.STATISTIC_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.statistic.StatisticService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        StatisticResolver::class,
    ],
)
class StatisticResolverTests {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var statisticService: StatisticService

    @Test
    fun statistics() {
        whenever(statisticService.findAll())
            .thenReturn(listOf(STATISTIC_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ statistics { id } }",
                "data.statistics[*].id",
            )

        assertThat(ids).contains(STATISTIC_FIXTURE_WITH_ID_1.id.toString())
    }
}
