package com.wirequery.manager.application.security

import com.wirequery.manager.domain.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

@ExtendWith(MockitoExtension::class)
class UserDetailsServiceImplTest {
    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var userDetailsService: UserDetailsServiceImpl

    @Test
    fun `loadUserByUsername finds user details by username from the service`() {
        val mockUserDetails = mock<UserDetails>()

        whenever(userService.findUserDetailsByUsername("admin"))
            .thenReturn(mockUserDetails)

        assertThat(userDetailsService.loadUserByUsername("admin"))
            .isEqualTo(mockUserDetails)
    }

    @Test
    fun `loadUserByUsername throws error if user details not found in service`() {
        whenever(userService.findUserDetailsByUsername("admin"))
            .thenReturn(null)

        assertThrows<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername("admin")
        }
    }
}
