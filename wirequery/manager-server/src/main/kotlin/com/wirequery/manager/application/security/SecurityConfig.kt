// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.security

import com.wirequery.manager.domain.access.AccessService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.task.TaskDecorator
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.context.request.RequestContextHolder
import java.util.concurrent.Executor

@Configuration
class SecurityConfig : AsyncConfigurer {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10)
    }

    @Bean
    fun authenticationProvider(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder,
    ): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

    @Bean
    @Primary
    override fun getAsyncExecutor(): Executor {
        val poolExecutor = ThreadPoolTaskExecutor()
        poolExecutor.setTaskDecorator(ContextCopyingDecorator())
        poolExecutor.initialize()
        return poolExecutor
    }

    @Bean
    fun accessService(accessService: AccessService): AccessService {
        return accessService
    }

    class ContextCopyingDecorator : TaskDecorator {
        override fun decorate(runnable: Runnable): Runnable {
            val context = RequestContextHolder.currentRequestAttributes()
            val securityContext = SecurityContextHolder.getContext()
            return Runnable {
                try {
                    RequestContextHolder.setRequestAttributes(context)
                    SecurityContextHolder.setContext(securityContext)
                    runnable.run()
                } finally {
                    RequestContextHolder.resetRequestAttributes()
                    SecurityContextHolder.clearContext()
                }
            }
        }
    }
}
