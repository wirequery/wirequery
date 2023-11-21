// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.role

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataLoader
import com.wirequery.manager.domain.role.Role
import com.wirequery.manager.domain.role.RoleService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executor

@DgsComponent
class RoleDataLoader(
    private val roleService: RoleService,
    private val executor: Executor,
) {
    @DgsDataLoader(name = "roleById")
    val roleById =
        MappedBatchLoader<Int, Role?> {
            supplyAsync({
                roleService.findByIds(it).associateBy { it.id }
            }, executor)
        }
}
