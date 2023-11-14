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
        "@accessService.isAuthorisedByApplicationIds(#applicationIds, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_STORED_QUERY)",
    )
    override fun load(applicationIds: MutableSet<Int>): CompletionStage<Map<Int, List<StoredQuery>>> =
        supplyAsync({
            storedQueryService.findByApplicationIds(applicationIds).groupBy { it.applicationId }
        }, executor)
}