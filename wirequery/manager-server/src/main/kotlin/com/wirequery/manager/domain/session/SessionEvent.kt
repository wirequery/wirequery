// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.session

import org.springframework.context.ApplicationEvent

sealed class SessionEvent(source: Any) : ApplicationEvent(source) {
    data class SessionsCreatedEvent(
        private val _source: Any,
        val entities: List<Session>,
        val originalTemplateIds: Map<Session, Int>,
    ) : SessionEvent(_source)

    data class SessionsUpdatedEvent(private val _source: Any, val entities: List<Session>) : SessionEvent(_source)

    data class SessionsDeletedEvent(private val _source: Any, val entities: List<Session>) : SessionEvent(_source)
}
