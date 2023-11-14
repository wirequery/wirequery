// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import com.wirequery.manager.domain.session.SessionEvent.SessionsDeletedEvent
import com.wirequery.manager.domain.template.TemplateEvent.TemplatesDeletedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class RecordingListener(
    private val recordingService: RecordingService,
) {
    @EventListener
    fun onEvent(event: SessionsDeletedEvent) {
        val entityIds = event.entities.mapNotNull { it.id }
        recordingService.deleteBySessionIds(entityIds)
    }

    @EventListener
    fun onEvent(event: TemplatesDeletedEvent) {
        val entityIds = event.entities.mapNotNull { it.id }
        recordingService.deleteByTemplateIds(entityIds)
    }
}
