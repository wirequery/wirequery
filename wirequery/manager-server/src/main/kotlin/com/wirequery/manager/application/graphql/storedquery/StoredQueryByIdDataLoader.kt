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
        "@accessService.isAuthorisedByStoredQueryIds(#storedQueryIds, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_STORED_QUERY)",
    )
    override fun load(storedQueryIds: MutableSet<Int>): CompletionStage<Map<Int, StoredQuery>> =
        CompletableFuture.supplyAsync({
            storedQueryService.findByIds(storedQueryIds).associateBy { it.id }
        }, executor)
}
