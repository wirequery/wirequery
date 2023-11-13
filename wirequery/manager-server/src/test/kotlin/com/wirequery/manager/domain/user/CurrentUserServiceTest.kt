package com.wirequery.manager.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

@ExtendWith(MockitoExtension::class)
internal class CurrentUserServiceTest {
    @InjectMocks
    private lateinit var currentUserService: CurrentUserService

    @Test
    fun `findCurrentUsername returns current username`() {
        val authentication = mock<Authentication>()
        whenever(authentication.name).thenReturn(SOME_USERNAME)
        val securityContext = Mockito.mock(SecurityContext::class.java)
        whenever(securityContext.authentication)
            .thenReturn(authentication)
        SecurityContextHolder.setContext(securityContext)
        val actual = currentUserService.findCurrentUsername()
        assertThat(actual).isEqualTo(SOME_USERNAME)
    }

    @Test
    fun `findCurrentUsername returns null if there is no authentication`() {
        val securityContext = Mockito.mock(SecurityContext::class.java)
        SecurityContextHolder.setContext(securityContext)
        val actual = currentUserService.findCurrentUsername()
        assertThat(actual).isEqualTo(null)
    }

    private companion object {
        const val SOME_USERNAME = "some_username"
    }
}
