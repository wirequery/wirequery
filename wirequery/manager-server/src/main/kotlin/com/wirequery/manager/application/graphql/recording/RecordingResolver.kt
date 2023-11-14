// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.recording

import com.netflix.graphql.dgs.*
import com.wirequery.manager.domain.recording.Recording
import com.wirequery.manager.domain.recording.RecordingService
import com.wirequery.manager.domain.recording.RecordingService.RecordingFilterInput
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
@PreAuthorize("isAuthenticated()")
class RecordingResolver(
    private val recordingService: RecordingService,
) {
    @DgsQuery
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_SESSIONS.name())")
    fun recordings(filter: RecordingFilterInput): Iterable<Recording> {
        return recordingService.findAll(filter)
    }
}
