// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.tenant

import java.time.LocalDateTime
import java.time.ZoneId

object TenantFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    private val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val TENANT_FIXTURE_WITH_ID_1 =
        Tenant(
            id = 1,
            name = "Some name",
            slug = "Some slug",
            plan = "Some plan",
            enabled = true,
            createdAt = OFFSET_DATE_TIME_FIXTURE,
        )

    val TENANT_ENTITY_FIXTURE_1 =
        TenantEntity(
            id = null,
            name = "Some name",
            slug = "Some slug",
            plan = "Some plan",
            enabled = true,
        )

    val CREATE_TENANT_FIXTURE_1 =
        TenantService.CreateTenantInput(
            name = "Some name",
            slug = "Some slug",
            plan = "Some plan",
            enabled = true,
        )

    val UPDATE_TENANT_FIXTURE_1 =
        TenantService.UpdateTenantInput(
            name = "Some name",
            slug = "Some slug",
            plan = "Some plan",
            enabled = true,
        )

    val TENANT_ENTITY_FIXTURE_WITH_ID_1 =
        TENANT_ENTITY_FIXTURE_1.copy(
            id = 1,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
