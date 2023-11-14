// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.storedquery

import com.wirequery.manager.domain.application.ApplicationEvent.ApplicationsUnquarantinedEvent
import com.wirequery.manager.domain.application.ApplicationEvent.BeforeApplicationsDeletedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StoredQueryListener(
    private val storedQueryService: StoredQueryService,
) {
    @EventListener
    fun onEvent(event: BeforeApplicationsDeletedEvent) {
        val entityIds = event.entities.map { it.id }
        storedQueryService.stopQueryingByApplicationIds(entityIds)
    }

    @EventListener
    fun onEvent(event: ApplicationsUnquarantinedEvent) {
        storedQueryService.restartQuerying(event.entities)
    }
}
