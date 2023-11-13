package com.wirequery.manager.domain.user

import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.role.RoleFixtures.ROLE_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.role.RoleService
import com.wirequery.manager.domain.tenant.TenantService
import com.wirequery.manager.domain.user.UserFixtures.REGISTER_USER_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.UPDATE_CURRENT_USER_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.UPDATE_USER_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.USER_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.user.UserFixtures.USER_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.user.UserFixtures.USER_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.user.UserFixtures.USER_ROLE_ENTITY_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.relational.core.conversion.DbActionExecutionException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class UserServiceUnitTests {
    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var roleService: RoleService

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var currentUserService: CurrentUserService

    @Mock
    private lateinit var tenantService: TenantService

    @InjectMocks
    private lateinit var userService: UserService

    @BeforeEach
    fun init() {
        lenient().`when`(tenantService.tenantId).thenReturn(0)

        lenient().`when`(roleService.findByIds(listOf(USER_ENTITY_FIXTURE_WITH_ID_1.userRoles.single().roleId)))
            .thenReturn(listOf(ROLE_FIXTURE_WITH_ID_1))

        lenient().`when`(roleService.findByNames(setOf(USER_FIXTURE_WITH_ID_1.roles)))
            .thenReturn(setOf(ROLE_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findById returns the mapped value of findById in UserRepository if it is non-empty`() {
        whenever(userRepository.findById(1))
            .thenReturn(Optional.of(USER_ENTITY_FIXTURE_WITH_ID_1))

        val actual = userService.findById(1)

        assertThat(actual).isEqualTo(USER_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findById returns null if findById in UserRepository yields an empty Optional`() {
        whenever(userRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = userService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findCurrentUser returns the current user`() {
        whenever(currentUserService.findCurrentUsername())
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1.username)

        whenever(userRepository.findByUsername(any()))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        val actual = userService.findCurrentUser()

        assertThat(actual).isEqualTo(USER_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findCurrentUser returns null if there is no current user`() {
        whenever(currentUserService.findCurrentUsername())
            .thenReturn(null)

        val actual = userService.findCurrentUser()

        assertThat(actual).isEqualTo(null)

        verify(userRepository, times(0)).findByUsername(any())
    }

    @Test
    fun `findByUsername returns the mapped value of findByUsername in UserRepository if it is non-empty`() {
        whenever(userRepository.findByUsername("username"))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        val actual = userService.findByUsername("username")

        assertThat(actual).isEqualTo(USER_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findByUsername returns null if findByUsername in UserRepository yields a value`() {
        whenever(userRepository.findByUsername("username"))
            .thenReturn(null)

        val actual = userService.findByUsername("username")

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findUserDetailsByUsername returns the mapped value of findByUsername in UserRepository if it is non-empty`() {
        whenever(userRepository.findByUsername("username"))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        val actual = userService.findUserDetailsByUsername("username")

        assertThat(actual?.username).isEqualTo(USER_FIXTURE_WITH_ID_1.username)
        assertThat(actual?.password).isEqualTo(USER_FIXTURE_WITH_ID_1.password)
    }

    @Test
    fun `findUserDetailsByUsername returns null if findByUsername in UserRepository yields a value`() {
        whenever(userRepository.findByUsername("username"))
            .thenReturn(null)

        val actual = userService.findUserDetailsByUsername("username")

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findByIds returns the mapped values of findByIds in UserRepository`() {
        whenever(userRepository.findByIds(listOf(1)))
            .thenReturn(listOf(USER_ENTITY_FIXTURE_WITH_ID_1))

        val actual = userService.findByIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(USER_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findAll returns the values of findAll in UserRepository`() {
        whenever(userRepository.findAll())
            .thenReturn(listOf(USER_ENTITY_FIXTURE_WITH_ID_1))

        val actual = userService.findAll()

        assertThat(actual).containsExactly(USER_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `register calls save on repository if all requirements are met and publishes an event`() {
        whenever(userRepository.save(USER_ENTITY_FIXTURE_1))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        whenever(passwordEncoder.encode(USER_ENTITY_FIXTURE_1.password))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1.password)

        val actual = userService.register(REGISTER_USER_FIXTURE_1)

        assertThat(actual).isEqualTo(USER_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(UserEvent.UsersRegisteredEvent(userService, listOf(USER_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `create catches duplicate errors and throws the corresponding functional exception`() {
        doThrow(DbActionExecutionException(mock(), DuplicateKeyException("")))
            .whenever(userRepository)
            .save(USER_ENTITY_FIXTURE_1)

        whenever(passwordEncoder.encode(USER_ENTITY_FIXTURE_1.password))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1.password)

        val actual =
            assertThrows<FunctionalException> {
                userService.register(REGISTER_USER_FIXTURE_1)
            }

        assertThat(actual.message).isEqualTo("A user with username ${USER_ENTITY_FIXTURE_1.username} already exists.")

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `update calls save on repository if all requirements are met and publishes an event`() {
        whenever(userRepository.findById(1))
            .thenReturn(Optional.of(USER_ENTITY_FIXTURE_WITH_ID_1))

        whenever(passwordEncoder.encode(USER_ENTITY_FIXTURE_1.password))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1.password)

        whenever(userRepository.save(USER_ENTITY_FIXTURE_WITH_ID_1.copy(userRoles = setOf(USER_ROLE_ENTITY_1))))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        val actual = userService.update(1, UPDATE_USER_FIXTURE_1)

        assertThat(actual).isEqualTo(USER_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(UserEvent.UsersUpdatedEvent(userService, listOf(USER_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `update does not update password when blank`() {
        whenever(userRepository.findById(1))
            .thenReturn(Optional.of(USER_ENTITY_FIXTURE_WITH_ID_1))

        whenever(userRepository.save(USER_ENTITY_FIXTURE_WITH_ID_1.copy(userRoles = setOf(USER_ROLE_ENTITY_1))))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        val actual = userService.update(1, UPDATE_USER_FIXTURE_1.copy(password = null))

        assertThat(actual).isEqualTo(USER_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(UserEvent.UsersUpdatedEvent(userService, listOf(USER_FIXTURE_WITH_ID_1)))

        verify(passwordEncoder, times(0)).encode(any())
    }

    @Test
    fun `updateCurrentUser calls save on repository if all requirements are met and publishes an event`() {
        whenever(currentUserService.findCurrentUsername())
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1.username)

        whenever(userRepository.findByUsername(USER_ENTITY_FIXTURE_WITH_ID_1.username))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        whenever(passwordEncoder.encode(USER_ENTITY_FIXTURE_1.password))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1.password)

        whenever(userRepository.save(USER_ENTITY_FIXTURE_WITH_ID_1))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        val actual = userService.updateCurrentUser(UPDATE_CURRENT_USER_FIXTURE_1)

        assertThat(actual).isEqualTo(USER_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(UserEvent.UsersUpdatedEvent(userService, listOf(USER_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `updateCurrentUser does not update password when blank`() {
        whenever(currentUserService.findCurrentUsername())
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1.username)

        whenever(userRepository.findByUsername(USER_ENTITY_FIXTURE_WITH_ID_1.username))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        whenever(userRepository.save(USER_ENTITY_FIXTURE_WITH_ID_1))
            .thenReturn(USER_ENTITY_FIXTURE_WITH_ID_1)

        val actual = userService.updateCurrentUser(UPDATE_CURRENT_USER_FIXTURE_1.copy(password = null))

        assertThat(actual).isEqualTo(USER_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(UserEvent.UsersUpdatedEvent(userService, listOf(USER_FIXTURE_WITH_ID_1)))

        verify(passwordEncoder, times(0)).encode(any())
    }

    @Test
    fun `deleteById deletes the User identified by id in the repository if it exists and publishes an event`() {
        whenever(userRepository.findById(1))
            .thenReturn(Optional.of(USER_ENTITY_FIXTURE_WITH_ID_1))

        userService.deleteById(1)

        verify(userRepository).deleteById(1)

        verify(publisher)
            .publishEvent(UserEvent.UsersDeletedEvent(userService, listOf(USER_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `deleteById does not delete the User identified by id in the repository if it doesn't exist and publishes no events`() {
        whenever(userRepository.findById(1))
            .thenReturn(Optional.empty())

        userService.deleteById(1)

        verify(userRepository, times(0)).deleteById(1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Nested
    inner class UserInputValidatorTest {
        @Test
        fun `passwords must be at least 6 characters`() {
            assertThrows<FunctionalException> {
                UserService.UserInputValidator.validatePassword(password = "aBCDE")
            }
            assertDoesNotThrow {
                UserService.UserInputValidator.validatePassword(password = "aBCDEF")
            }
        }

        @Test
        fun `passwords must include both upper and lower case characters`() {
            assertThrows<FunctionalException> {
                UserService.UserInputValidator.validatePassword(password = "abcde")
            }
            assertThrows<FunctionalException> {
                UserService.UserInputValidator.validatePassword(password = "ABCDE")
            }
            assertDoesNotThrow {
                UserService.UserInputValidator.validatePassword(password = "aBCDEF")
            }
        }

        @Test
        fun `passwords may not start or end with a space`() {
            assertThrows<FunctionalException> {
                UserService.UserInputValidator.validatePassword(password = " aBCDE")
            }
            assertThrows<FunctionalException> {
                UserService.UserInputValidator.validatePassword(password = "aBCDE ")
            }
            assertDoesNotThrow {
                UserService.UserInputValidator.validatePassword(password = "aBCDEF")
            }
        }
    }

    @Nested
    inner class RegisterUserInputTest {
        @Test
        fun `passwords are validated`() {
            assertThrows<FunctionalException> {
                REGISTER_USER_FIXTURE_1.copy(password = "aBCDE")
            }
            assertDoesNotThrow {
                REGISTER_USER_FIXTURE_1.copy(password = "aBCDEF")
            }
        }
    }

    @Nested
    inner class UpdateUserInputTest {
        @Test
        fun `passwords are validated`() {
            assertThrows<FunctionalException> {
                UPDATE_USER_FIXTURE_1.copy(password = "aBCDE")
            }
            assertDoesNotThrow {
                UPDATE_USER_FIXTURE_1.copy(password = "aBCDEF")
            }
        }
    }

    @Nested
    inner class UpdateCurrentUserInputTest {
        @Test
        fun `passwords are validated`() {
            assertThrows<FunctionalException> {
                UPDATE_CURRENT_USER_FIXTURE_1.copy(password = "aBCDE")
            }
            assertDoesNotThrow {
                UPDATE_CURRENT_USER_FIXTURE_1.copy(password = "aBCDEF")
            }
        }
    }
}
