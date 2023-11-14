// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.role

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.role.RoleFixtures.CREATE_ROLE_FIXTURE_1
import com.wirequery.manager.domain.role.RoleFixtures.UPDATE_ROLE_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class RoleIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var roleService: RoleService

    @Test
    fun `Roles can be created, updated, fetched and deleted`() {
        var role = roleService.create(CREATE_ROLE_FIXTURE_1)
        role = roleService.update(role.id, UPDATE_ROLE_FIXTURE_1)!!

        assertThat(roleService.findAll()).isNotEmpty
        assertThat(roleService.findById(role.id)).isNotNull
        assertThat(roleService.findByIds(listOf(role.id))).isNotEmpty

        roleService.deleteById(role.id)

        assertThat(roleService.findAll()).isEmpty()
    }
}
