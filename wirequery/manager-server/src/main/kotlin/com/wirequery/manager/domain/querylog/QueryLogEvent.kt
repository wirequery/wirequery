package com.wirequery.manager.domain.querylog

import org.springframework.context.ApplicationEvent

sealed class QueryLogEvent(source: Any) : ApplicationEvent(source) {
    data class QueryLogsCreatedEvent(private val _source: Any, val entities: List<QueryLog>) : QueryLogEvent(_source)

    data class QueryLogsFetchedEvent(private val _source: Any, val entities: List<QueryLog>) : QueryLogEvent(_source)
}
