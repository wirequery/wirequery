package com.wirequery.manager.domain.application

import org.springframework.context.ApplicationEvent as AE

sealed class ApplicationEvent(source: Any) : AE(source) {
    data class ApplicationsCreatedEvent(private val _source: Any, val entities: List<Application>) :
        ApplicationEvent(_source)

    data class ApplicationsUpdatedEvent(private val _source: Any, val entities: List<Application>) :
        ApplicationEvent(_source)

    data class BeforeApplicationsDeletedEvent(private val _source: Any, val entities: List<Application>) :
        ApplicationEvent(_source)

    data class ApplicationsDeletedEvent(private val _source: Any, val entities: List<Application>) :
        ApplicationEvent(_source)

    data class ApplicationsQuarantinedEvent(
        private val _source: Any,
        val quarantineRule: String,
        val quarantineReason: String,
        val entities: List<Application>,
    ) : ApplicationEvent(_source)

    data class ApplicationsUnquarantinedEvent(
        private val _source: Any,
        val unquarantineReason: String,
        val entities: List<Application>,
    ) : ApplicationEvent(_source)

    data class ApplicationsApiKeyRequestedEvent(
        private val _source: Any,
        val entities: List<Application>,
    ) : ApplicationEvent(_source)
}
