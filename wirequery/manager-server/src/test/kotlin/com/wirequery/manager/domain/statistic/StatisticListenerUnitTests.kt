// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import com.wirequery.manager.domain.querylog.QueryLogEvent
import com.wirequery.manager.domain.querylog.QueryLogFixtures.QUERY_LOG_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.recording.RecordingEvent
import com.wirequery.manager.domain.recording.RecordingFixtures.RECORDING_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.user.UserEvent
import com.wirequery.manager.domain.user.UserFixtures.USER_FIXTURE_WITH_ID_1
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class StatisticListenerUnitTests {
    @Mock
    private lateinit var statisticService: StatisticService

    @InjectMocks
    private lateinit var statisticListener: StatisticListener

    @Test
    fun `When QueryLogsCreatedEvent is triggered, the related statistic is incremented`() {
        statisticListener.onEvent(QueryLogEvent.QueryLogsCreatedEvent(this, listOf(QUERY_LOG_FIXTURE_WITH_ID_1)))

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.QUERY_LOG,
                    metadata =
                        mapOf(
                            "storedQueryId" to "" + QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                            "appName" to QUERY_LOG_FIXTURE_WITH_ID_1.appName,
                            "length" to "" + QUERY_LOG_FIXTURE_WITH_ID_1.message.length,
                        ),
                    amount = 1,
                ),
            )
    }

    @Test
    fun `When UsersLoggedInEvent is triggered, the related statistic is incremented`() {
        statisticListener.onEvent(UserEvent.UsersLoggedInEvent(this, listOf(USER_FIXTURE_WITH_ID_1.username)))

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.LOGIN,
                    metadata =
                        mapOf(
                            "username" to "" + USER_FIXTURE_WITH_ID_1.username,
                        ),
                    amount = 1,
                ),
            )
    }

    @Test
    fun `When RecordingsCreatedEvent is triggered, the related statistic is incremented`() {
        statisticListener.onEvent(RecordingEvent.RecordingsCreatedEvent(this, listOf(RECORDING_FIXTURE_WITH_ID_1)))

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.RECORDING,
                    metadata =
                        mapOf(
                            "id" to "" + RECORDING_FIXTURE_WITH_ID_1.id,
                            "sessionId" to "" + RECORDING_FIXTURE_WITH_ID_1.sessionId,
                        ),
                    amount = 1,
                ),
            )
    }
}
