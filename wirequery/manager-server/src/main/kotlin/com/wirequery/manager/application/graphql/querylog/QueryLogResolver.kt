// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.querylog

import com.netflix.graphql.dgs.*
import com.wirequery.manager.domain.querylog.QueryLog
import com.wirequery.manager.domain.querylog.QueryLogService
import com.wirequery.manager.domain.querylog.QueryLogService.QueryLogFilterInput
import com.wirequery.manager.domain.querylog.QueryLogService.TraceFilterInput
import com.wirequery.manager.domain.storedquery.StoredQuery
import org.springframework.security.access.prepost.PreAuthorize
import java.util.concurrent.CompletableFuture

@DgsComponent
@PreAuthorize("isAuthenticated()")
class QueryLogResolver(
    private val queryLogService: QueryLogService,
) {
    @DgsQuery
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_QUERY_LOGS)
               || @accessService.isAuthorisedByStoredQueryId(#filter.storedQueryId, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_QUERY_LOGS)""",
    )
    fun queryLogs(filter: QueryLogFilterInput): Iterable<QueryLog> {
        return queryLogService.findMainLogs(filter)
    }

    @DgsQuery
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_QUERY_LOGS)
               || @accessService.isAuthorisedByStoredQueryId(#filter.storedQueryId, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_QUERY_LOGS)""",
    )
    fun queryLogByTrace(filter: TraceFilterInput): Iterable<QueryLog> {
        return queryLogService.findByTraceId(filter.storedQueryId, filter.traceId)
    }

    @DgsData(parentType = "QueryLog", field = "storedQuery")
    fun storedQuery(dfe: DgsDataFetchingEnvironment): CompletableFuture<StoredQuery> {
        val queryLog = dfe.getSource<QueryLog>()
        return dfe.getDataLoader<Int, StoredQuery?>("storedQueryById")
            .load(queryLog.storedQueryId)
    }

    @DgsData(parentType = "StoredQuery", field = "queryLogs")
    fun queryLogsByStoredQuery(dfe: DgsDataFetchingEnvironment): CompletableFuture<Iterable<QueryLog>> {
        val storedQuery = dfe.getSource<StoredQuery>()
        val storedQueryId = storedQuery.id
        return dfe.getDataLoader<Int, Iterable<QueryLog>>("queryLogsByStoredQueryId")
            .load(storedQueryId)
            .thenApply { it ?: listOf() }
    }
}
