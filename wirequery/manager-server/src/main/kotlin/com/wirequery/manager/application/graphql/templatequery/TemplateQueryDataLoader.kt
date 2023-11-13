package com.wirequery.manager.application.graphql.templatequery

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataLoader
import com.wirequery.manager.domain.templatequery.TemplateQuery
import com.wirequery.manager.domain.templatequery.TemplateQueryService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executor

@DgsComponent
class TemplateQueryDataLoader(
    private val templateQueryService: TemplateQueryService,
    private val executor: Executor,
) {
    @DgsDataLoader(name = "templateQueryById")
    val templateQueryById =
        MappedBatchLoader<Int, TemplateQuery?> {
            supplyAsync({
                templateQueryService.findByIds(it).associateBy { it.id }
            }, executor)
        }

    @DgsDataLoader(name = "templateQuerysByTemplateId")
    val templateQuerysByTemplateId =
        MappedBatchLoader<Int, List<TemplateQuery>> {
            supplyAsync({
                templateQueryService.findByTemplateIds(it).groupBy { it.templateId }
            }, executor)
        }

    @DgsDataLoader(name = "templateQuerysByApplicationId")
    val templateQuerysByApplicationId =
        MappedBatchLoader<Int, List<TemplateQuery>> {
            supplyAsync({
                templateQueryService.findByApplicationIds(it).groupBy { it.applicationId }
            }, executor)
        }
}
