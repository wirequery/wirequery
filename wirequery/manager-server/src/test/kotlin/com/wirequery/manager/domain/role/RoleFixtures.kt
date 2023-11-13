package com.wirequery.manager.domain.role

import java.time.LocalDateTime
import java.time.ZoneId

object RoleFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    private val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val ROLE_FIXTURE_WITH_ID_1 =
        Role(
            id = 20,
            name = "Some role",
            authorisationNames = listOf("Some authorisation"),
            createdAt = OFFSET_DATE_TIME_FIXTURE,
        )

    val ROLE_ENTITY_FIXTURE_1 =
        RoleEntity(
            id = null,
            name = "Some role",
            authorisations = setOf(RoleEntity.RoleAuthorisation("Some authorisation")),
        )

    val CREATE_ROLE_FIXTURE_1 =
        RoleService.CreateRoleInput(
            name = "Some role",
            authorisationNames = listOf("Some authorisation"),
        )

    val UPDATE_ROLE_FIXTURE_1 =
        RoleService.UpdateRoleInput(
            name = "Some role",
            authorisationNames = listOf("Some authorisation"),
        )

    val ROLE_ENTITY_FIXTURE_WITH_ID_1 =
        ROLE_ENTITY_FIXTURE_1.copy(
            id = 20,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
