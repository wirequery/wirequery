package com.wirequery.manager.domain.templatequery

import org.springframework.context.ApplicationEvent

sealed class TemplateQueryEvent(source: Any) : ApplicationEvent(source) {
    data class TemplateQuerysCreatedEvent(private val _source: Any, val entities: List<TemplateQuery>) :
        TemplateQueryEvent(_source)

    data class TemplateQuerysUpdatedEvent(private val _source: Any, val entities: List<TemplateQuery>) :
        TemplateQueryEvent(_source)

    data class TemplateQuerysDeletedEvent(private val _source: Any, val entities: List<TemplateQuery>) :
        TemplateQueryEvent(_source)
}
