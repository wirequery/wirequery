// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import com.wirequery.manager.domain.statistic.StatisticFixtures.INCREMENT_STATISTIC_FIXTURE_1
import com.wirequery.manager.domain.statistic.StatisticFixtures.SET_STATISTIC_FIXTURE_1
import com.wirequery.manager.domain.statistic.StatisticFixtures.STATISTIC_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.statistic.StatisticFixtures.STATISTIC_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.statistic.StatisticFixtures.STATISTIC_FIXTURE_WITH_ID_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.*
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class StatisticServiceUnitTests {
    @Mock
    private lateinit var statisticRepository: StatisticRepository

    @Mock
    private lateinit var clock: Clock

    @InjectMocks
    private lateinit var statisticService: StatisticService

    @Test
    fun `findAll returns the values of findAll in StatisticRepository`() {
        whenever(statisticRepository.findAll())
            .thenReturn(listOf(STATISTIC_ENTITY_FIXTURE_WITH_ID_1))

        val actual = statisticService.findAll()

        assertThat(actual).containsExactly(STATISTIC_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `increment calls incrementOrCreate on repository if all requirements are met and publishes an event`() {
        whenever(clock.zone).thenReturn(ZoneId.systemDefault())

        whenever(clock.instant()).thenReturn(Instant.now())

        whenever(statisticRepository.incrementOrCreate(any(), any(), any(), any(), any()))
            .thenReturn(true)

        statisticService.increment(INCREMENT_STATISTIC_FIXTURE_1)

        val moment = LocalDateTime.now(clock)

        verify(statisticRepository)
            .incrementOrCreate(
                moment = moment.toLocalDate(),
                hour = moment.hour,
                type = STATISTIC_ENTITY_FIXTURE_1.type.name,
                metadata = STATISTIC_ENTITY_FIXTURE_1.metadata,
                amount = STATISTIC_ENTITY_FIXTURE_1.amount,
            )
    }

    @Test
    fun `set calls replace on repository if all requirements are met and publishes an event`() {
        whenever(clock.zone).thenReturn(ZoneId.systemDefault())

        whenever(clock.instant()).thenReturn(Instant.now())

        whenever(statisticRepository.replace(any(), any(), any(), any(), any()))
            .thenReturn(true)

        statisticService.set(SET_STATISTIC_FIXTURE_1)

        val moment = LocalDateTime.now(clock)

        verify(statisticRepository)
            .replace(
                moment = moment.toLocalDate(),
                hour = moment.hour,
                type = STATISTIC_ENTITY_FIXTURE_1.type.name,
                metadata = STATISTIC_ENTITY_FIXTURE_1.metadata,
                amount = STATISTIC_ENTITY_FIXTURE_1.amount,
            )
    }
}
