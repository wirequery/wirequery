// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

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
