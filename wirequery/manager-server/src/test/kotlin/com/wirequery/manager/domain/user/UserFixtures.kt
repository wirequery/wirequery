package com.wirequery.manager.domain.user

import java.time.LocalDateTime
import java.time.ZoneId

object UserFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    private val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val USER_FIXTURE_WITH_ID_1 =
        User(
            id = 1,
            username = "Some username",
            password = "Some password",
            enabled = true,
            roles = "Some role",
            createdAt = OFFSET_DATE_TIME_FIXTURE,
        )

    val USER_ROLE_ENTITY_1 =
        UserEntity.UserRoleEntity(
            roleId = 20,
        )

    val USER_ROLE_ENTITY_WITH_ID_1 =
        UserEntity.UserRoleEntity(
            id = 2,
            roleId = 20,
        )

    val USER_ENTITY_FIXTURE_1 =
        UserEntity(
            id = null,
            username = "Some username",
            password = "Some password",
            enabled = true,
            userRoles = setOf(USER_ROLE_ENTITY_1),
            tenantId = 0,
        )

    val USER_ENTITY_WITHOUT_ROLES_FIXTURE_1 =
        UserEntity(
            id = null,
            username = "Some username",
            password = "Some password",
            enabled = true,
            userRoles = setOf(),
            tenantId = 0,
        )

    val LOGIN_USER_FIXTURE_1 =
        UserService.LoginInput(
            username = "Some username",
            password = "Some password",
        )

    val REGISTER_USER_FIXTURE_1 =
        UserService.RegisterInput(
            username = "Some username",
            password = "Some password",
            enabled = true,
            roles = "Some role",
        )

    val UPDATE_USER_FIXTURE_1 =
        UserService.UpdateUserInput(
            password = "Some password",
            enabled = true,
            roles = "Some role",
        )

    val UPDATE_CURRENT_USER_FIXTURE_1 =
        UserService.UpdateCurrentUserInput(
            password = "Some password",
        )

    val USER_ENTITY_FIXTURE_WITH_ID_1 =
        USER_ENTITY_FIXTURE_1.copy(
            id = 1,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
            userRoles = setOf(USER_ROLE_ENTITY_WITH_ID_1),
        )
}
