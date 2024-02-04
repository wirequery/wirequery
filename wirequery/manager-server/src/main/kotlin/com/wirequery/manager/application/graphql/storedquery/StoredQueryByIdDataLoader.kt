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
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

@DgsDataLoader(name = "storedQueryById")
class StoredQueryByIdDataLoader(
    private val storedQueryService: StoredQueryService,
    private val executor: Executor,
) : MappedBatchLoader<Int, StoredQuery> {
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_STORED_QUERIES)
               || @accessService.isAuthorisedByStoredQueryIds(#storedQueryIds, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_STORED_QUERY)""",
    )
    override fun load(storedQueryIds: MutableSet<Int>): CompletionStage<Map<Int, StoredQuery>> =
        CompletableFuture.supplyAsync({
            storedQueryService.findByIds(storedQueryIds).associateBy { it.id }
        }, executor)
}
