// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.templatequery

import java.time.LocalDateTime
import java.time.ZoneId

object TemplateQueryFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    private val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val TEMPLATE_QUERY_FIXTURE_WITH_ID_1 =
        TemplateQuery(
            id = 1,
            templateId = 10,
            applicationId = 10,
            nameTemplate = "Some nameTemplate",
            queryTemplate = "Some queryTemplate",
            queryLimit = 1,
            createdAt = OFFSET_DATE_TIME_FIXTURE,
        )

    val TEMPLATE_QUERY_ENTITY_FIXTURE_1 =
        TemplateQueryEntity(
            id = null,
            templateId = 10,
            applicationId = 10,
            nameTemplate = "Some nameTemplate",
            queryTemplate = "Some queryTemplate",
            queryLimit = 1,
        )

    val CREATE_TEMPLATE_QUERY_FIXTURE_1 =
        TemplateQueryService.CreateTemplateQueryInput(
            templateId = 10,
            nameTemplate = "Some nameTemplate",
            queryTemplate = "Some queryTemplate",
            queryLimit = 1,
        )

    val UPDATE_TEMPLATE_QUERY_FIXTURE_1 =
        TemplateQueryService.UpdateTemplateQueryInput(
            templateId = 10,
            nameTemplate = "Some nameTemplate",
            queryTemplate = "Some queryTemplate",
            queryLimit = 1,
        )

    val TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1 =
        TEMPLATE_QUERY_ENTITY_FIXTURE_1.copy(
            id = 1,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
