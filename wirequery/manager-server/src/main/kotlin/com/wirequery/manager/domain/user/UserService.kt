package com.wirequery.manager.domain.user

import com.wirequery.manager.application.security.CustomUserDetails
import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.FunctionalException.Companion.checkFunctional
import com.wirequery.manager.domain.role.RoleService
import com.wirequery.manager.domain.tenant.TenantService
import com.wirequery.manager.domain.user.UserEvent.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.relational.core.conversion.DbActionExecutionException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val roleService: RoleService,
    private val passwordEncoder: PasswordEncoder,
    private val publisher: ApplicationEventPublisher,
    private val currentUserService: CurrentUserService,
    private val tenantService: TenantService,
) {
    fun findById(id: Int): User? {
        return userRepository.findByIdOrNull(id)
            ?.let(::toDomainObject)
    }

    fun findCurrentUser(): User? {
        return currentUserService.findCurrentUsername()?.let(::findByUsername)
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
            ?.let(::toDomainObject)
    }

    fun findUserDetailsByUsername(username: String): UserDetails? {
        return userRepository.findByUsername(username)?.let { userEntity ->
            val user = toDomainObject(userEntity)
            return CustomUserDetails(
                username = userEntity.username,
                authorities = findAuthorisationNames(user).map { a -> GrantedAuthority { a } },
                isEnabled = userEntity.enabled,
                password = userEntity.password,
                tenantId = userEntity.tenantId,
            )
        }
    }

    fun findByIds(ids: Iterable<Int>): List<User> {
        return userRepository.findByIds(ids)
            .map(::toDomainObject)
    }

    fun findAll(): List<User> {
        return userRepository.findAll()
            .map(::toDomainObject)
    }

    fun register(input: RegisterInput): User {
        try {
            // TODO refactor into a util method + update tests
            val userEntity =
                UserEntity(
                    username = input.username,
                    password = passwordEncoder.encode(input.password),
                    enabled = input.enabled,
                    userRoles = roleNamesToIds(input.roles).map { UserEntity.UserRoleEntity(roleId = it) }.toSet(),
                    tenantId = checkNotNull(tenantService.tenantId),
                )
            val user = toDomainObject(userRepository.save(userEntity))
            publisher.publishEvent(UsersRegisteredEvent(this, listOf(user)))
            return user
        } catch (e: DbActionExecutionException) {
            if (e.cause is DuplicateKeyException) {
                throw FunctionalException(
                    "A user with username ${input.username} already exists.",
                    e,
                )
            }
            throw e
        }
    }

    fun update(
        id: Int,
        input: UpdateUserInput,
    ): User? {
        val userEntity = userRepository.findByIdOrNull(id) ?: return null
        val user =
            userRepository.save(
                userEntity.copy(
                    password =
                        if (input.password == null) {
                            userEntity.password
                        } else {
                            passwordEncoder.encode(input.password)
                        },
                    enabled = input.enabled ?: userEntity.enabled,
                    userRoles =
                        input.roles
                            ?.let(::roleNamesToIds)
                            ?.map { UserEntity.UserRoleEntity(roleId = it) }
                            ?.toSet()
                            ?: userEntity.userRoles,
                ),
            ).let(::toDomainObject)
        publisher.publishEvent(UsersUpdatedEvent(this, listOf(user)))
        return user
    }

    fun updateCurrentUser(input: UpdateCurrentUserInput): User? {
        val userEntity =
            currentUserService.findCurrentUsername()
                ?.let(userRepository::findByUsername)
                ?: return null
        val user =
            userRepository.save(
                userEntity.copy(
                    password =
                        if (input.password.isNullOrBlank()) {
                            userEntity.password
                        } else {
                            passwordEncoder.encode(input.password)
                        },
                ),
            ).let(::toDomainObject)
        publisher.publishEvent(UsersUpdatedEvent(this, listOf(user)))
        return user
    }

    fun deleteById(id: Int): Boolean {
        val user =
            userRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return false
        userRepository.deleteById(id)
        publisher.publishEvent(UsersDeletedEvent(this, listOf(user)))
        return true
    }

    private fun toDomainObject(entity: UserEntity) =
        User(
            id = entity.id!!,
            username = entity.username,
            password = entity.password,
            enabled = entity.enabled,
            roles = roleIdsToNames(entity.userRoles.map { it.roleId }),
            createdAt =
                entity.createdAt!!
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
            updatedAt =
                entity.updatedAt
                    ?.atZone(ZoneId.systemDefault())
                    ?.toOffsetDateTime(),
            createdBy = entity.createdBy,
            updatedBy = entity.updatedBy,
        )

    private fun roleNamesToIds(roles: String): List<Int> {
        return roleService.findByNames(roles.split(",").toSet()).map { it.id }
    }

    private fun roleIdsToNames(roleIds: Collection<Int>): String {
        return roleService.findByIds(roleIds).map { it.name }.joinToString(",")
    }

    fun findAuthorisationNamesByUsers(users: Set<User>): Map<User, List<String>> {
        val roles = roleService.findAll().groupBy { it.name }
        return users.associateWith { user ->
            user.roles.split(",")
                .flatMap { roles[it] ?: listOf() }
                .flatMap { it.authorisationNames }
                .distinct()
        }
    }

    fun findAuthorisationNames(user: User): List<String> {
        val roles = roleService.findAll().groupBy { it.name }
        return user.roles.split(",")
            .flatMap { roles[it] ?: listOf() }
            .flatMap { it.authorisationNames }
            .distinct()
    }

    data class LoginInput(
        val username: String,
        val password: String,
    )

    data class RegisterInput(
        val username: String,
        val password: String,
        val enabled: Boolean,
        val roles: String,
    ) {
        init {
            UserInputValidator.validatePassword(password)
        }
    }

    data class UpdateUserInput(
        val password: String?,
        val enabled: Boolean?,
        val roles: String?,
    ) {
        init {
            if (password != null) {
                UserInputValidator.validatePassword(password)
            }
        }
    }

    data class UpdateCurrentUserInput(
        val password: String?,
    ) {
        init {
            if (password != null) {
                UserInputValidator.validatePassword(password)
            }
        }
    }

    object UserInputValidator {
        fun validatePassword(password: String) {
            checkFunctional(password.trim().length >= 6) { "Password is too short" }
            checkFunctional(!password.startsWith(" ")) { "Password may not start with a space" }
            checkFunctional(!password.endsWith(" ")) { "Password may not end with a space" }
            checkFunctional(password.uppercase() != password) { "Password must contain both upper and lower case characters" }
            checkFunctional(password.lowercase() != password) { "Password must contain both upper and lower case characters" }
        }
    }
}
