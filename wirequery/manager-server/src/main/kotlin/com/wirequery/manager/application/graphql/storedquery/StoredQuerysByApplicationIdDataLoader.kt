// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.storedquery

import com.netflix.graphql.dgs.DgsDataLoader
import com.wirequery.manager.domain.storedquery.StoredQuery
import com.wirequery.manager.domain.storedquery.StoredQueryService
import org.dataloader.MappedBatchLoader
import org.springframework.security.access.prepost.PreAuthorize
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

@DgsDataLoader(name = "storedQuerysByApplicationId")
class StoredQuerysByApplicationIdDataLoader(
    private val storedQueryService: StoredQueryService,
    private val executor: Executor,
) : MappedBatchLoader<Int, List<StoredQuery>> {
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_STORED_QUERIES)
               || @accessService.isAuthorisedByApplicationIds(#applicationIds, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_STORED_QUERY)""",
    )
    override fun load(applicationIds: MutableSet<Int>): CompletionStage<Map<Int, List<StoredQuery>>> =
        supplyAsync({
            storedQueryService.findByApplicationIds(applicationIds).groupBy { it.applicationId }
        }, executor)
}
