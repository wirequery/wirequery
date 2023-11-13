package com.wirequery.manager.domain.role

import org.springframework.context.ApplicationEvent

sealed class RoleEvent(source: Any) : ApplicationEvent(source) {
    data class RolesCreatedEvent(private val _source: Any, val entities: List<Role>) : RoleEvent(_source)

    data class RolesUpdatedEvent(private val _source: Any, val entities: List<Role>) : RoleEvent(_source)

    data class RolesDeletedEvent(private val _source: Any, val entities: List<Role>) : RoleEvent(_source)
}
