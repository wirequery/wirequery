package com.wirequery.manager.application.rest

import com.wirequery.manager.domain.session.SessionService
import com.wirequery.manager.domain.storedquery.StoredQueryService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class JobController(
    private val storedQueryService: StoredQueryService,
    private val sessionService: SessionService,
) {
    @PostMapping("/api/internal/jobs")
    fun triggerJobs() {
        storedQueryService.disableOverdueQueries()
        sessionService.deleteOldDrafts()
    }
}
