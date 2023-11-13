package com.wirequery.manager.application.graphql.user

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.FakeSecurityConfig
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.user.User
import com.wirequery.manager.domain.user.UserFixtures.LOGIN_USER_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.REGISTER_USER_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.UPDATE_CURRENT_USER_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.UPDATE_USER_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.USER_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        UserResolver::class,
        UserDataLoader::class,
        GraphQLExceptionHandler::class,
    ],
)
@Import(FakeSecurityConfig::class)
class UserResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var authenticationProvider: AuthenticationProvider

    @MockBean
    private lateinit var userService: UserService

    @Test
    fun `currentUser returns current user if authorized`() {
        whenever(userService.findCurrentUser())
            .thenReturn(USER_FIXTURE_WITH_ID_1)

        val id =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "{ currentUser { id } }",
                "data.currentUser.id",
            )

        assertThat(id).contains(USER_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `currentUser throws error if not authorized`() {
        whenever(securityContext.authentication)
            .thenReturn(null)

        assertThrows<QueryException> {
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "{ currentUser { id } }",
                "data.currentUser.id",
            )
        }
    }

    @Test
    fun `user returns user if authorized`() {
        whenever(userService.findById(USER_FIXTURE_WITH_ID_1.id))
            .thenReturn(USER_FIXTURE_WITH_ID_1)

        val id =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "query user(\$id: ID!) { user(id: \$id) { id } }",
                "data.user.id",
                mapOf("id" to "" + USER_FIXTURE_WITH_ID_1.id),
            )

        assertThat(id).isEqualTo(USER_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `user throws error if not authorized`() {
        whenever(securityContext.authentication)
            .thenReturn(null)

        assertThrows<QueryException> {
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "query user(\$id: ID!) { user(id: \$id) { id } }",
                "data.user.id",
                mapOf("id" to "" + USER_FIXTURE_WITH_ID_1.id),
            )
        }
    }

    @Test
    fun `users returns users if authorized`() {
        whenever(userService.findAll())
            .thenReturn(listOf(USER_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ users { id } }",
                "data.users[*].id",
            )

        assertThat(ids).contains(USER_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `users throws error if not authorized`() {
        whenever(securityContext.authentication)
            .thenReturn(null)

        assertThrows<QueryException> {
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ users { id } }",
                "data.users[*].id",
            )
        }
    }

    @Test
    fun `login returns null on failure and does not log set security context`() {
        val oldContext = SecurityContextHolder.getContext()

        try {
            SecurityContextHolder.setContext(mock())

            whenever(authenticationProvider.authenticate(any()))
                .thenThrow(DisabledException("Account disabled"))

            val loginInput =
                mapOf(
                    "username" to "Some username",
                    "password" to "Some password",
                )

            val result =
                dgsQueryExecutor.executeAndExtractJsonPath<User?>(
                    "mutation login(\$input: LoginInput!) { login(input: \$input) { id } }",
                    "data.login",
                    mapOf("input" to loginInput),
                )

            assertThat(result).isEqualTo(null)

            verify(authenticationProvider)
                .authenticate(
                    UsernamePasswordAuthenticationToken(
                        LOGIN_USER_FIXTURE_1.username,
                        LOGIN_USER_FIXTURE_1.password,
                    ),
                )

            verifyNoMoreInteractions(userService, SecurityContextHolder.getContext())
        } finally {
            SecurityContextHolder.setContext(oldContext)
        }
    }

    @Test
    fun `login sets security context if login was successful`() {
        val oldContext = SecurityContextHolder.getContext()

        try {
            val authenticationMock = mock<Authentication>()

            SecurityContextHolder.setContext(mock())

            whenever(authenticationProvider.authenticate(any()))
                .thenReturn(authenticationMock)

            whenever(userService.findCurrentUser())
                .thenReturn(USER_FIXTURE_WITH_ID_1)

            val loginInput =
                mapOf(
                    "username" to "Some username",
                    "password" to "Some password",
                )

            val result =
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation login(\$input: LoginInput!) { login(input: \$input) { id } }",
                    "data.login.id",
                    mapOf("input" to loginInput),
                )

            assertThat(result).isEqualTo(USER_FIXTURE_WITH_ID_1.id.toString())

            verify(authenticationProvider)
                .authenticate(
                    UsernamePasswordAuthenticationToken(
                        LOGIN_USER_FIXTURE_1.username,
                        LOGIN_USER_FIXTURE_1.password,
                    ),
                )

            verify(SecurityContextHolder.getContext())
                .authentication = authenticationMock
        } finally {
            SecurityContextHolder.setContext(oldContext)
        }
    }

    @Test
    fun `logout sets security context if login was successful`() {
        val oldContext = SecurityContextHolder.getContext()

        try {
            SecurityContextHolder.setContext(mock())

            val result =
                dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                    "mutation logout { logout }",
                    "data.logout",
                )

            assertThat(result).isEqualTo(true)

            verify(SecurityContextHolder.getContext()).authentication = null
        } finally {
            SecurityContextHolder.setContext(oldContext)
        }
    }

    @Test
    fun `register calls register if the user has MANAGE_ROLES authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.MANAGE_USERS.name }))

        whenever(userService.register(any()))
            .thenReturn(USER_FIXTURE_WITH_ID_1)

        val registerInput =
            mapOf(
                "username" to "Some username",
                "password" to "Some password",
                "enabled" to true,
                "roles" to "Some role",
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation register(\$input: RegisterInput!) { register(input: \$input) { id } }",
                "data.register.id",
                mapOf("input" to registerInput),
            )

        assertThat(result).isEqualTo(USER_FIXTURE_WITH_ID_1.id.toString())

        verify(userService).register(REGISTER_USER_FIXTURE_1)
    }

    @Test
    fun `register does not call register if the user does not have MANAGE_ROLES authorisation`() {
        val registerInput =
            mapOf(
                "username" to "Some username",
                "password" to "Some password",
                "enabled" to true,
                "roles" to "Some roles",
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation register(\$input: RegisterInput!) { register(input: \$input) { id } }",
                    "data.register.id",
                    mapOf("input" to registerInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(userService, times(0)).register(any())
    }

    @Test
    fun `updateUser calls update if the user has MANAGE_USERS authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.MANAGE_USERS.name }))

        whenever(userService.update(anyInt(), any()))
            .thenReturn(USER_FIXTURE_WITH_ID_1)

        val updateUserInput =
            mapOf(
                "password" to "Some password",
                "enabled" to true,
                "roles" to "Some role",
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation updateUser(\$id: ID!, \$input: UpdateUserInput!) { updateUser(id: \$id, input: \$input) { id } }",
                "data.updateUser.id",
                mapOf("id" to USER_FIXTURE_WITH_ID_1.id, "input" to updateUserInput),
            )

        assertThat(result).isEqualTo(USER_FIXTURE_WITH_ID_1.id.toString())

        verify(userService).update(USER_FIXTURE_WITH_ID_1.id, UPDATE_USER_FIXTURE_1)
    }

    @Test
    fun `updateUser does not call update if the user does not have MANAGE_USERS authorisation`() {
        val updateUserInput =
            mapOf(
                "password" to "Some password",
                "enabled" to true,
                "roles" to "Some roles",
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation updateUser(\$id: ID!, \$input: UpdateUserInput!) { updateUser(id: \$id, input: \$input) { id } }",
                    "data.updateUser.id",
                    mapOf("id" to USER_FIXTURE_WITH_ID_1.id, "input" to updateUserInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(userService, times(0)).update(anyInt(), any())
    }

    @Test
    fun `updateCurrentUser delegates the call to the service's updateCurrentUser`() {
        whenever(userService.updateCurrentUser(any()))
            .thenReturn(USER_FIXTURE_WITH_ID_1)

        val updateUserInput =
            mapOf(
                "password" to "Some password",
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation updateCurrentUser(\$input: UpdateCurrentUserInput!) { updateCurrentUser(input: \$input) { id } }",
                "data.updateCurrentUser.id",
                mapOf("id" to USER_FIXTURE_WITH_ID_1.id, "input" to updateUserInput),
            )

        assertThat(result).isEqualTo(USER_FIXTURE_WITH_ID_1.id.toString())

        verify(userService).updateCurrentUser(UPDATE_CURRENT_USER_FIXTURE_1)
    }

    @Test
    fun `deleteUser calls delete if the user has MANAGE_USERS authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.MANAGE_USERS.name }))

        whenever(userService.deleteById(any()))
            .thenReturn(true)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                "mutation deleteUser(\$id: ID!) { deleteUser(id: \$id) }",
                "data.deleteUser",
                mapOf("id" to 1),
            )

        assertThat(result).isEqualTo(true)

        verify(userService).deleteById(1)
    }

    @Test
    fun `deleteUser does not call delete if the user does not have MANAGE_USERS authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                    "mutation deleteUser(\$id: ID!) { deleteUser(id: \$id) }",
                    "data.deleteUser",
                    mapOf("id" to 1),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(userService, times(0)).deleteById(1)
    }
}
