package com.wirequery.manager.domain.session

import java.time.LocalDateTime
import java.time.ZoneId

object SessionFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    internal val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val SESSION_FIXTURE_WITH_ID_1 =
        Session(
            id = 1,
            name = "Some name",
            description = "Some description",
            createdAt = OFFSET_DATE_TIME_FIXTURE,
            draft = false,
        )

    val SESSION_ENTITY_FIXTURE_1 =
        SessionEntity(
            id = null,
            name = "Some name",
            description = "Some description",
            draft = false,
        )

    val CREATE_SESSION_FIXTURE_1 =
        SessionService.CreateSessionInput(
            templateId = 10,
            variables =
                listOf(
                    SessionService.CreateSessionInputFieldValue(
                        key = "customerId",
                        value = "Customer ID",
                    ),
                ),
            endDate = OFFSET_DATE_TIME_FIXTURE,
        )

    val SESSION_ENTITY_FIXTURE_WITH_ID_1 =
        SESSION_ENTITY_FIXTURE_1.copy(
            id = 1,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
