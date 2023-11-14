// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.storedquery

import com.wirequery.manager.domain.application.ApplicationEvent
import com.wirequery.manager.domain.application.ApplicationEvent.BeforeApplicationsDeletedEvent
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_FIXTURE_WITH_ID_1
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
internal class StoredQueryListenerUnitTests {
    @Mock
    private lateinit var storedQueryService: StoredQueryService

    @InjectMocks
    private lateinit var storedQueryListener: StoredQueryListener

    @Test
    fun `When ApplicationsDeletedEvent is triggered, related StoredQuerys are deleted`() {
        storedQueryListener.onEvent(BeforeApplicationsDeletedEvent(this, listOf(APPLICATION_FIXTURE_WITH_ID_1)))

        verify(storedQueryService).stopQueryingByApplicationIds(listOf(APPLICATION_FIXTURE_WITH_ID_1.id))
    }

    @Test
    fun `When ApplicationsUnquarantinedEvent is triggered, related stored queries are restarted`() {
        storedQueryListener.onEvent(
            ApplicationEvent.ApplicationsUnquarantinedEvent(
                this,
                "some reason",
                listOf(APPLICATION_FIXTURE_WITH_ID_1),
            ),
        )

        verify(storedQueryService).restartQuerying(listOf(APPLICATION_FIXTURE_WITH_ID_1))
    }
}
