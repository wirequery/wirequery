// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import org.springframework.context.ApplicationEvent

sealed class RecordingEvent(source: Any) : ApplicationEvent(source) {
    data class RecordingsCreatedEvent(private val _source: Any, val entities: List<Recording>) : RecordingEvent(_source)

    data class RecordingsUpdatedEvent(private val _source: Any, val entities: List<Recording>) : RecordingEvent(_source)

    data class RecordingsDeletedEvent(private val _source: Any, val entities: List<Recording>) : RecordingEvent(_source)
}
