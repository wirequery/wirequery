package com.wirequery.manager.domain.query

import org.springframework.context.ApplicationEvent

sealed class QueryEvent(source: Any) : ApplicationEvent(source) {
    data class QueryEnteredEvent(private val _source: Any, val query: String) : QueryEvent(_source)

    data class QueryReportedEvent(private val _source: Any, val report: QueryReport) : QueryEvent(_source)
}
