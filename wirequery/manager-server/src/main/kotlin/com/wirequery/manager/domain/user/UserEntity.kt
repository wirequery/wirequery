// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.user

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class UserEntity(
    @Id
    val id: Int? = null,
    val tenantId: Int,
    val username: String,
    val password: String,
    val enabled: Boolean,
    @MappedCollection(idColumn = "user_id")
    val userRoles: Set<UserRoleEntity>,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @CreatedBy
    val createdBy: String? = null,
    @LastModifiedBy
    val updatedBy: String? = null,
) {
    @Table("user_roles")
    data class UserRoleEntity(
        @Id
        val id: Int? = null,
        val roleId: Int,
    )
}
