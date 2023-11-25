// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.storedquery

import java.time.LocalDateTime
import java.time.ZoneId

object StoredQueryFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    private val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val STORED_QUERY_FIXTURE_WITH_ID_1 =
        StoredQuery(
            id = 1,
            sessionId = null,
            applicationId = 10,
            name = "Some name",
            type = StoredQuery.Type.QUERY,
            query = "SomeName GET 2xx",
            queryLimit = 1,
            endDate =
                LOCAL_DATE_TIME_FIXTURE.plusDays(3)
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
            createdAt = OFFSET_DATE_TIME_FIXTURE,
        )

    val STORED_QUERY_ENTITY_FIXTURE_1 =
        StoredQueryEntity(
            id = null,
            sessionId = null,
            applicationId = 10,
            name = "Some name",
            type = StoredQuery.Type.QUERY,
            query = "SomeName GET 2xx",
            queryLimit = 1,
            endDate = LOCAL_DATE_TIME_FIXTURE.plusDays(3),
            disabled = false,
        )

    val CREATE_STORED_QUERY_FIXTURE_1 =
        StoredQueryService.CreateStoredQueryInput(
            sessionId = null,
            name = "Some name",
            type = StoredQuery.Type.QUERY,
            query = "SomeName GET 2xx",
            queryLimit = 1,
            endDate =
                LOCAL_DATE_TIME_FIXTURE.plusDays(3)
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
        )

    val STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1 =
        STORED_QUERY_ENTITY_FIXTURE_1.copy(
            id = 1,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
