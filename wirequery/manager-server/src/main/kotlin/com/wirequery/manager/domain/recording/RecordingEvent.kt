package com.wirequery.manager.domain.recording

import org.springframework.context.ApplicationEvent

sealed class RecordingEvent(source: Any) : ApplicationEvent(source) {
    data class RecordingsCreatedEvent(private val _source: Any, val entities: List<Recording>) : RecordingEvent(_source)

    data class RecordingsUpdatedEvent(private val _source: Any, val entities: List<Recording>) : RecordingEvent(_source)

    data class RecordingsDeletedEvent(private val _source: Any, val entities: List<Recording>) : RecordingEvent(_source)
}
