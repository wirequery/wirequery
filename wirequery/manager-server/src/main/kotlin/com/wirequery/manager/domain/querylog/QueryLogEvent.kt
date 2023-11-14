// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import org.springframework.context.ApplicationEvent

sealed class QueryLogEvent(source: Any) : ApplicationEvent(source) {
    data class QueryLogsCreatedEvent(private val _source: Any, val entities: List<QueryLog>) : QueryLogEvent(_source)

    data class QueryLogsFetchedEvent(private val _source: Any, val entities: List<QueryLog>) : QueryLogEvent(_source)
}
