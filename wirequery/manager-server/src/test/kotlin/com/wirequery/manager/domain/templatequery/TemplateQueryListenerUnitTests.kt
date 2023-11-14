// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.templatequery

import com.wirequery.manager.domain.application.ApplicationEvent.ApplicationsDeletedEvent
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.template.TemplateEvent.TemplatesDeletedEvent
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_FIXTURE_WITH_ID_1
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
internal class TemplateQueryListenerUnitTests {
    @Mock
    private lateinit var templateQueryService: TemplateQueryService

    @InjectMocks
    private lateinit var templateQueryListener: TemplateQueryListener

    @Test
    fun `When TemplatesDeletedEvent is triggered, related TemplateQuerys are deleted`() {
        templateQueryListener.onEvent(TemplatesDeletedEvent(this, listOf(TEMPLATE_FIXTURE_WITH_ID_1)))

        verify(templateQueryService).deleteByTemplateIds(listOf(TEMPLATE_FIXTURE_WITH_ID_1.id))
    }

    @Test
    fun `When ApplicationsDeletedEvent is triggered, related TemplateQuerys are deleted`() {
        templateQueryListener.onEvent(ApplicationsDeletedEvent(this, listOf(APPLICATION_FIXTURE_WITH_ID_1)))

        verify(templateQueryService).deleteByApplicationIds(listOf(APPLICATION_FIXTURE_WITH_ID_1.id))
    }
}
