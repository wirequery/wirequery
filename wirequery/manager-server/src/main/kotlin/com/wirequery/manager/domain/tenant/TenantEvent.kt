// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.tenant

import org.springframework.context.ApplicationEvent

sealed class TenantEvent(source: Any) : ApplicationEvent(source) {
    data class TenantsCreatedEvent(private val _source: Any, val entities: List<Tenant>) : TenantEvent(_source)

    data class TenantsUpdatedEvent(private val _source: Any, val entities: List<Tenant>) : TenantEvent(_source)

    data class TenantsDeletedEvent(private val _source: Any, val entities: List<Tenant>) : TenantEvent(_source)
}
