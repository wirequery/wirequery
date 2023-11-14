// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.templatequery

import com.wirequery.manager.domain.application.ApplicationEvent.ApplicationsDeletedEvent
import com.wirequery.manager.domain.template.TemplateEvent.TemplatesDeletedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TemplateQueryListener(
    private val templateQueryService: TemplateQueryService,
) {
    @EventListener
    fun onEvent(event: TemplatesDeletedEvent) {
        val entityIds = event.entities.mapNotNull { it.id }
        templateQueryService.deleteByTemplateIds(entityIds)
    }

    @EventListener
    fun onEvent(event: ApplicationsDeletedEvent) {
        val entityIds = event.entities.mapNotNull { it.id }
        templateQueryService.deleteByApplicationIds(entityIds)
    }
}
