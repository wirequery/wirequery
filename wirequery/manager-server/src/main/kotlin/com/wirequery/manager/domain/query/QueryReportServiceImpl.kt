// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.query

import com.wirequery.manager.domain.global.PubSubService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class QueryReportServiceImpl(
    private val pubSubService: PubSubService,
    private val publisher: ApplicationEventPublisher,
) : QueryReportService {
    override fun reportQueryResults(queryReports: List<QueryReport>) {
        queryReports.forEach { queryReport ->
            publisher.publishEvent(QueryEvent.QueryReportedEvent(this, queryReport))
            if (!queryReport.queryId.endsWith(":trace")) {
                pubSubService.publish("query_report:${queryReport.queryId}", queryReport)
            }
        }
    }
}
