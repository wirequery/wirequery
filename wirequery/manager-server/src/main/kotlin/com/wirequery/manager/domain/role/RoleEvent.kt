// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.role

import org.springframework.context.ApplicationEvent

sealed class RoleEvent(source: Any) : ApplicationEvent(source) {
    data class RolesCreatedEvent(private val _source: Any, val entities: List<Role>) : RoleEvent(_source)

    data class RolesUpdatedEvent(private val _source: Any, val entities: List<Role>) : RoleEvent(_source)

    data class RolesDeletedEvent(private val _source: Any, val entities: List<Role>) : RoleEvent(_source)
}
