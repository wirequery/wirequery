package com.wirequery.manager.application.graphql.template

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataLoader
import com.wirequery.manager.domain.template.Template
import com.wirequery.manager.domain.template.TemplateService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executor

@DgsComponent
class TemplateDataLoader(
    private val templateService: TemplateService,
    private val executor: Executor,
) {
    @DgsDataLoader(name = "templateById")
    val templateById =
        MappedBatchLoader<Int, Template?> {
            supplyAsync({
                templateService.findByIds(it).associateBy { it.id }
            }, executor)
        }
}
