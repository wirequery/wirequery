// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.user

import org.springframework.context.ApplicationEvent

sealed class UserEvent(source: Any) : ApplicationEvent(source) {
    data class UsersRegisteredEvent(private val _source: Any, val entities: List<User>) : UserEvent(_source)

    data class UsersUpdatedEvent(private val _source: Any, val entities: List<User>) : UserEvent(_source)

    data class UsersDeletedEvent(private val _source: Any, val entities: List<User>) : UserEvent(_source)
}
