package com.wirequery.manager.domain.storedquery

import org.springframework.context.ApplicationEvent

sealed class StoredQueryEvent(source: Any) : ApplicationEvent(source) {
    data class StoredQuerysCreatedEvent(private val _source: Any, val entities: List<StoredQuery>) :
        StoredQueryEvent(_source)

    data class StoredQuerysUpdatedEvent(private val _source: Any, val entities: List<StoredQuery>) :
        StoredQueryEvent(_source)

    data class StoredQuerysDeletedEvent(private val _source: Any, val entities: List<StoredQuery>) :
        StoredQueryEvent(_source)
}
