// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.querylog

import com.netflix.graphql.dgs.DgsDataLoader
import com.wirequery.manager.domain.querylog.QueryLog
import com.wirequery.manager.domain.querylog.QueryLogService
import org.dataloader.MappedBatchLoader
import org.springframework.security.access.prepost.PreAuthorize
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

@DgsDataLoader(name = "queryLogsByStoredQueryId")
class QueryLogsByStoredQueryIdDataLoader(
    private val queryLogService: QueryLogService,
    private val executor: Executor,
) : MappedBatchLoader<Int, List<QueryLog>> {
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_QUERY_LOGS)
               || @accessService.isAuthorisedByStoredQueryIds(#storedQueryIds, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_QUERY_LOGS)""",
    )
    override fun load(storedQueryIds: MutableSet<Int>): CompletionStage<Map<Int, List<QueryLog>>> =
        supplyAsync({
            queryLogService
                .findMainLogsByStoredQueryIds(storedQueryIds)
                .groupBy { it.storedQueryId }
        }, executor)
}
