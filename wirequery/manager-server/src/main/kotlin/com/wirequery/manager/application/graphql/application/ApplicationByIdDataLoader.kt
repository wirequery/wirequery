// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.application

import com.netflix.graphql.dgs.DgsDataLoader
import com.wirequery.manager.domain.application.Application
import com.wirequery.manager.domain.application.ApplicationService
import org.dataloader.MappedBatchLoader
import org.springframework.security.access.prepost.PreAuthorize
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

@DgsDataLoader(name = "applicationById")
class ApplicationByIdDataLoader(
    private val applicationService: ApplicationService,
    private val executor: Executor,
) : MappedBatchLoader<Int, Application> {
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_APPLICATIONS)
               || @accessService.isAuthorisedByApplicationIds(#ids, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_APPLICATION)""",
    )
    override fun load(ids: Set<Int>): CompletionStage<Map<Int, Application>> =
        supplyAsync({
            applicationService.findByIds(ids).associateBy { it.id }
        }, executor)
}
