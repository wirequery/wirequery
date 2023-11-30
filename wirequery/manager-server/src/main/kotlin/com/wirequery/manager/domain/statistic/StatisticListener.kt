// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import com.wirequery.manager.domain.querylog.QueryLogEvent
import com.wirequery.manager.domain.recording.RecordingEvent
import com.wirequery.manager.domain.statistic.Statistic.TypeEnum.*
import com.wirequery.manager.domain.user.UserEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StatisticListener(
    private val statisticService: StatisticService,
) {
    @EventListener
    fun onEvent(event: QueryLogEvent.QueryLogsCreatedEvent) {
        event.entities.forEach {
            statisticService.increment(
                StatisticService.IncrementStatisticInput(
                    type = QUERY_LOG,
                    metadata =
                        mapOf(
                            "storedQueryId" to "" + it.storedQueryId,
                            "appName" to it.appName,
                        ),
                    amount = 1,
                ),
            )

            statisticService.increment(
                StatisticService.IncrementStatisticInput(
                    type = QUERY_LOG,
                    metadata =
                        mapOf(
                            "storedQueryId" to "" + it.storedQueryId,
                            "appName" to it.appName,
                        ),
                    amount = it.message.length / 4096 + 1,
                ),
            )
        }
    }

    @EventListener
    fun onEvent(event: UserEvent.UsersLoggedInEvent) {
        event.usernames.forEach {
            statisticService.increment(
                StatisticService.IncrementStatisticInput(
                    type = LOGIN,
                    metadata =
                        mapOf(
                            "username" to it,
                        ),
                    amount = 1,
                ),
            )
        }
    }

    @EventListener
    fun onEvent(event: RecordingEvent.RecordingsCreatedEvent) {
        event.entities.forEach {
            statisticService.increment(
                StatisticService.IncrementStatisticInput(
                    type = RECORDING,
                    metadata =
                        mapOf(
                            "id" to "" + it.id,
                            "sessionId" to "" + it.sessionId,
                        ),
                    amount = 1,
                ),
            )
        }
    }
}
