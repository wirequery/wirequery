package com.wirequery.manager.domain.template

import org.springframework.context.ApplicationEvent

sealed class TemplateEvent(source: Any) : ApplicationEvent(source) {
    data class TemplatesCreatedEvent(private val _source: Any, val entities: List<Template>) : TemplateEvent(_source)

    data class TemplatesUpdatedEvent(private val _source: Any, val entities: List<Template>) : TemplateEvent(_source)

    data class TemplatesDeletedEvent(private val _source: Any, val entities: List<Template>) : TemplateEvent(_source)
}
