// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import com.wirequery.manager.domain.query.QueryEvent
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.query.QueryReport
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class StatisticListenerUnitTests {
    @Mock
    private lateinit var statisticService: StatisticService

    @Mock
    private lateinit var queryParserService: QueryParserService

    @InjectMocks
    private lateinit var statisticListener: StatisticListener

    @Test
    fun `When QueryEnteredEvent is triggered, the related statistic is incremented`() {
        val parsedQuery = mock<QueryParserService.Query>()

        whenever(parsedQuery.queryHead)
            .thenReturn(QueryParserService.QueryHead("app", "GET", "", "2xx"))

        whenever(queryParserService.parse("app | GET 2xx"))
            .thenReturn(parsedQuery)

        statisticListener.onEvent(
            QueryEvent.QueryEnteredEvent(this, "app | GET 2xx"),
        )

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.INSTANT_QUERY,
                    metadata =
                        mapOf(
                            "appName" to "app",
                        ),
                    amount = 1,
                ),
            )
    }

    @Test
    fun `When QueryReportedEvent is triggered, the related statistics are incremented`() {
        statisticListener.onEvent(
            QueryEvent.QueryReportedEvent(
                this,
                QueryReport(
                    appName = "app",
                    queryId = "123",
                    message = " ".repeat(4095),
                    startTime = 0,
                    endTime = 0,
                    traceId = null,
                    requestCorrelationId = null,
                ),
            ),
        )

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.INSTANT_QUERY_LOG,
                    metadata =
                        mapOf(
                            "appName" to "app",
                        ),
                    amount = 1,
                ),
            )

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.INSTANT_QUERY_LOG_CHUNKS,
                    metadata =
                        mapOf(
                            "appName" to "app",
                        ),
                    amount = 1,
                ),
            )
    }

    @Test
    fun `When QueryReportedEvent is triggered, chunks are incremented by 2 for each multiple of 4096`() {
        statisticListener.onEvent(
            QueryEvent.QueryReportedEvent(
                this,
                QueryReport(
                    appName = "app",
                    queryId = "123",
                    message = " ".repeat(4096),
                    startTime = 0,
                    endTime = 0,
                    traceId = null,
                    requestCorrelationId = null,
                ),
            ),
        )

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.INSTANT_QUERY_LOG,
                    metadata =
                        mapOf(
                            "appName" to "app",
                        ),
                    amount = 1,
                ),
            )

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.INSTANT_QUERY_LOG_CHUNKS,
                    metadata =
                        mapOf(
                            "appName" to "app",
                        ),
                    amount = 2,
                ),
            )
    }

    @Test
    fun `When QueryLogsCreatedEvent is triggered, the related statistics are incremented`() {
        statisticListener.onEvent(
            QueryLogEvent.QueryLogsCreatedEvent(
                this,
                listOf(QUERY_LOG_FIXTURE_WITH_ID_1.copy(message = " ".repeat(4095))),
            ),
        )

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.QUERY_LOG,
                    metadata =
                        mapOf(
                            "storedQueryId" to "" + QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                            "appName" to QUERY_LOG_FIXTURE_WITH_ID_1.appName,
                        ),
                    amount = 1,
                ),
            )

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.QUERY_LOG_CHUNKS,
                    metadata =
                        mapOf(
                            "storedQueryId" to "" + QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                            "appName" to QUERY_LOG_FIXTURE_WITH_ID_1.appName,
                        ),
                    amount = 1,
                ),
            )
    }

    @Test
    fun `When QueryLogsCreatedEvent is triggered, chunks are incremented by 2 for each multiple of 4096`() {
        statisticListener.onEvent(
            QueryLogEvent.QueryLogsCreatedEvent(
                this,
                listOf(QUERY_LOG_FIXTURE_WITH_ID_1.copy(message = " ".repeat(4096))),
            ),
        )

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.QUERY_LOG,
                    metadata =
                        mapOf(
                            "storedQueryId" to "" + QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                            "appName" to QUERY_LOG_FIXTURE_WITH_ID_1.appName,
                        ),
                    amount = 1,
                ),
            )

        verify(statisticService)
            .increment(
                StatisticService.IncrementStatisticInput(
                    type = Statistic.TypeEnum.QUERY_LOG_CHUNKS,
                    metadata =
                        mapOf(
                            "storedQueryId" to "" + QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                            "appName" to QUERY_LOG_FIXTURE_WITH_ID_1.appName,
                        ),
                    amount = 2,
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
