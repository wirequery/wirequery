// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application

import com.wirequery.manager.application.security.SecurityConfig
import com.wirequery.manager.domain.access.AccessService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

@Import(ResolverTestContext.Config::class)
@EnableMethodSecurity(prePostEnabled = true)
abstract class ResolverTestContext {
    @MockBean
    protected lateinit var authenticationManager: AuthenticationManager

    @MockBean
    protected lateinit var securityContext: SecurityContext

    protected lateinit var authenticationMock: Authentication

    @TestConfiguration
    class Config {
        @Bean
        fun executor(): ThreadPoolTaskExecutor {
            val poolExecutor = ThreadPoolTaskExecutor()
            poolExecutor.setTaskDecorator(SecurityConfig.ContextCopyingDecorator())
            poolExecutor.initialize()
            return poolExecutor
        }

        @Bean
        fun accessService(): AccessService {
            return mock()
        }
    }

    @BeforeEach
    fun init() {
        authenticationMock = mock()
        whenever(authenticationMock.isAuthenticated)
            .thenReturn(true)

        whenever(authenticationManager.authenticate(any()))
            .thenReturn(authenticationMock)

        whenever(authenticationMock.name)
            .thenReturn("Some Username")

        whenever(securityContext.authentication)
            .thenReturn(authenticationMock)

        SecurityContextHolder.setContext(securityContext)
    }
}
