// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import com.wirequery.manager.domain.query.AggregatorService
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.query.QueryReport
import com.wirequery.manager.domain.storedquery.StoredQuery
import com.wirequery.manager.domain.storedquery.StoredQueryService
import org.springframework.stereotype.Service

@Service
class QueryLogAggregationService(
    private val storedQueryService: StoredQueryService,
    private val queryParserService: QueryParserService,
    private val aggregatorService: AggregatorService,
) {
    // NOTE this solution will not work for parameterized aggregators
    fun toAggregatedDomainObjects(queryLogs: List<QueryLog>): List<QueryLog> {
        val storedQueryIds = queryLogs.map { it.storedQueryId }.distinct()
        val storedQueries = storedQueryService.findByIds(storedQueryIds)
        val queryLogsByStoredQueryId = queryLogs.groupBy { it.storedQueryId }
        val results = mutableListOf<QueryLog>()

        storedQueries.forEach { storedQuery ->
            val parsedQuery = queryParserService.parse(storedQuery.query)
            val aggregator = aggregatorService.create(parsedQuery)

            queryLogsByStoredQueryId[storedQuery.id]!!.forEach { queryLog ->
                val aggregatorResult = aggregator.apply(toQueryReport(queryLog, storedQuery))
                if (aggregatorResult != null) {
                    results.add(fromQueryReportAndQueryLog(aggregatorResult, queryLog))
                }
            }
        }

        return results
    }

    private fun toQueryReport(
        queryLog: QueryLog,
        storedQuery: StoredQuery,
    ): QueryReport {
        return QueryReport(
            appName = IRRELEVANT_FOR_AGGREGATOR,
            queryId = StoredQueryService.STORED_QUERY_PREFIX + storedQuery.id,
            message = queryLog.message,
            startTime = queryLog.startTime,
            endTime = queryLog.endTime,
            traceId = queryLog.traceId,
            requestCorrelationId = queryLog.requestCorrelationId,
        )
    }

    private fun fromQueryReportAndQueryLog(
        aggregatorResult: QueryReport,
        queryLog: QueryLog,
    ): QueryLog {
        return queryLog.copy(
            message = aggregatorResult.message,
        )
    }

    companion object {
        val IRRELEVANT_FOR_AGGREGATOR = "IRRELEVANT FOR AGGREGATOR"
    }
}
