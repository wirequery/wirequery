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
