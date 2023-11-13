package com.wirequery.manager.application.graphql

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationProvider

@TestConfiguration
class FakeSecurityConfig {
    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authenticationProvider = mock<AuthenticationProvider>()
        whenever(authenticationProvider.authenticate(any())).thenReturn(mock())
        return authenticationProvider
    }
}
