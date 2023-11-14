// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.session

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataLoader
import com.wirequery.manager.domain.session.Session
import com.wirequery.manager.domain.session.SessionService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executor

@DgsComponent
class SessionDataLoader(
    private val sessionService: SessionService,
    private val executor: Executor,
) {
    @DgsDataLoader(name = "sessionById")
    val sessionById =
        MappedBatchLoader<Int, Session?> {
            supplyAsync({
                sessionService.findByIds(it).associateBy { it.id }
            }, executor)
        }
}
