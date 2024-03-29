// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

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
            recordingCorrelationId = "abc",
        )

    val SESSION_ENTITY_FIXTURE_WITH_ID_1 =
        SESSION_ENTITY_FIXTURE_1.copy(
            id = 1,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
