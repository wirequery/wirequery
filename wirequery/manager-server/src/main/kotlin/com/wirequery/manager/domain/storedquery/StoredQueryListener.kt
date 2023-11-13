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
