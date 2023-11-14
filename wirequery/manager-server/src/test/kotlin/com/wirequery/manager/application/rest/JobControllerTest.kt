// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.rest

import com.wirequery.manager.domain.session.SessionService
import com.wirequery.manager.domain.storedquery.StoredQueryService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class JobControllerTest {
    @Mock
    private lateinit var storedQueryService: StoredQueryService

    @Mock
    private lateinit var sessionService: SessionService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun init() {
        mockMvc =
            MockMvcBuilders
                .standaloneSetup(JobController(storedQueryService, sessionService))
                .build()
    }

    @Test
    fun `disableOverdueQueries is called on a call to triggerJobs`() {
        mockMvc.post("/api/internal/jobs")
            .andExpect { status { isOk() } }

        verify(storedQueryService).disableOverdueQueries()
    }

    @Test
    fun `deleteOldDrafts is called on a call to triggerJobs`() {
        mockMvc.post("/api/internal/jobs")
            .andExpect { status { isOk() } }

        verify(sessionService).deleteOldDrafts()
    }
}
