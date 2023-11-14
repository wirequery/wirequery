// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import com.wirequery.manager.domain.session.SessionEvent.SessionsDeletedEvent
import com.wirequery.manager.domain.session.SessionFixtures.SESSION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.template.TemplateEvent.TemplatesDeletedEvent
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_FIXTURE_WITH_ID_1
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
internal class RecordingListenerUnitTests {
    @Mock
    private lateinit var recordingService: RecordingService

    @InjectMocks
    private lateinit var recordingListener: RecordingListener

    @Test
    fun `When SessionsDeletedEvent is triggered, related Recordings are deleted`() {
        recordingListener.onEvent(SessionsDeletedEvent(this, listOf(SESSION_FIXTURE_WITH_ID_1)))

        verify(recordingService).deleteBySessionIds(listOf(SESSION_FIXTURE_WITH_ID_1.id))
    }

    @Test
    fun `When TemplatesDeletedEvent is triggered, related Recordings are deleted`() {
        recordingListener.onEvent(TemplatesDeletedEvent(this, listOf(TEMPLATE_FIXTURE_WITH_ID_1)))

        verify(recordingService).deleteByTemplateIds(listOf(TEMPLATE_FIXTURE_WITH_ID_1.id))
    }
}
