// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.user

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.user.UserFixtures.REGISTER_USER_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.UPDATE_CURRENT_USER_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.UPDATE_USER_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

class UserIntegrationTests : IntegrationTestContext() {
    @MockBean
    private lateinit var currentUserService: CurrentUserService

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `Users can be registered, updated, fetched and deleted without errors`() {
        whenever(currentUserService.findCurrentUsername())
            .thenReturn(REGISTER_USER_FIXTURE_1.username)

        var user = userService.register(REGISTER_USER_FIXTURE_1)
        userService.update(user.id, UPDATE_USER_FIXTURE_1)!!
        user = userService.updateCurrentUser(UPDATE_CURRENT_USER_FIXTURE_1)!!

        assertThat(userService.findAll()).isNotEmpty
        assertThat(userService.findCurrentUser()).isNotNull
        assertThat(userService.findById(user.id)).isNotNull
        assertThat(userService.findByIds(listOf(user.id))).isNotEmpty

        userService.deleteById(user.id)

        assertThat(userService.findAll()).isEmpty()
    }
}
