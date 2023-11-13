package com.wirequery.manager.domain.application

import java.time.LocalDateTime
import java.time.ZoneId

object ApplicationFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    private val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val APPLICATION_FIXTURE_WITH_ID_1 =
        Application(
            id = 1,
            name = "SomeName",
            description = "Some description",
            apiKey = "Some apiKey",
            inQuarantine = false,
            quarantineRule = null,
            quarantineReason = null,
            createdAt = OFFSET_DATE_TIME_FIXTURE,
        )

    val APPLICATION_ENTITY_FIXTURE_1 =
        ApplicationEntity(
            id = null,
            name = "SomeName",
            description = "Some description",
            inQuarantine = false,
            quarantineRule = null,
            quarantineReason = null,
            apiKey = "Some apiKey",
        )

    val CREATE_APPLICATION_FIXTURE_1 =
        ApplicationService.CreateApplicationInput(
            name = "SomeName",
            description = "Some description",
        )

    val UPDATE_APPLICATION_FIXTURE_1 =
        ApplicationService.UpdateApplicationInput(
            description = "Some description",
        )

    val APPLICATION_ENTITY_FIXTURE_WITH_ID_1 =
        APPLICATION_ENTITY_FIXTURE_1.copy(
            id = 1,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
