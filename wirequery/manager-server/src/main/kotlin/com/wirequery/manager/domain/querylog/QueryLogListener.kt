// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import com.wirequery.manager.domain.global.PubSubService
import com.wirequery.manager.domain.query.QueryEvent.QueryReportedEvent
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.query.QueryService.QueryMutation.QueryOneTrace
import com.wirequery.manager.domain.querylog.QueryLogService.CreateQueryLogInput
import com.wirequery.manager.domain.storedquery.StoredQueryService
import com.wirequery.manager.domain.storedquery.StoredQueryService.Companion.STORED_QUERY_PREFIX
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class QueryLogListener(
    private val queryLogService: QueryLogService,
    private val storedQueryService: StoredQueryService,
    private val queryParserService: QueryParserService,
    private val pubSubService: PubSubService,
) {
    @EventListener
    fun onEvent(event: QueryReportedEvent) {
        if (!event.report.queryId.startsWith(STORED_QUERY_PREFIX)) {
            return
        }

        val queryReport = event.report

        val storedQueryId = event.report.queryId.split(":")[1].toInt()

        if (!queryReport.queryId.endsWith(":trace") && queryReport.traceId != null) {
            val storedQuery = storedQueryService.findById(storedQueryId)
            val trace = storedQuery?.let { queryParserService.parse(storedQuery.query).queryHead.trace } ?: false
            if (trace) {
                // Gather additional logs for tracing.
                pubSubService.publish(
                    "trace_requests",
                    QueryOneTrace(queryReport.queryId + ":trace", queryReport.traceId),
                )
            }
        }

        if (queryReport.queryId.endsWith(":trace")) {
            queryLogService.putTrace(queryReport)
            return
        } else {
            queryLogService.logOrDisableIfLimitsReached(
                CreateQueryLogInput(
                    storedQueryId = storedQueryId,
                    message = queryReport.message,
                    startTime = queryReport.startTime,
                    endTime = queryReport.endTime,
                    traceId = queryReport.traceId,
                    appName = queryReport.appName,
                ),
            )
        }
    }
}
