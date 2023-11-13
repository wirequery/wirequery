package com.wirequery.manager.domain.tenant

import org.springframework.context.ApplicationEvent

sealed class TenantEvent(source: Any) : ApplicationEvent(source) {
    data class TenantsCreatedEvent(private val _source: Any, val entities: List<Tenant>) : TenantEvent(_source)

    data class TenantsUpdatedEvent(private val _source: Any, val entities: List<Tenant>) : TenantEvent(_source)

    data class TenantsDeletedEvent(private val _source: Any, val entities: List<Tenant>) : TenantEvent(_source)
}
