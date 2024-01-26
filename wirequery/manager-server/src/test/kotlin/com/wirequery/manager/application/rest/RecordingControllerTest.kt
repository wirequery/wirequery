// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wirequery.manager.application.rest.RecordingController.CancelRecordingInput
import com.wirequery.manager.application.rest.RecordingController.FinishRecordingInput
import com.wirequery.manager.domain.recording.RecordingFixtures.RECORDING_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.recording.RecordingFixtures.RECORDING_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.recording.RecordingFixtures.START_RECORDING_FIXTURE_1
import com.wirequery.manager.domain.recording.RecordingService
import com.wirequery.manager.domain.template.TemplateService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.function.RequestPredicates.accept

@ExtendWith(MockitoExtension::class)
class RecordingControllerTest {
    @Mock
    private lateinit var recordingService: RecordingService

    @Mock
    private lateinit var templateService: TemplateService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun init() {
        mockMvc =
            MockMvcBuilders
                .standaloneSetup(RecordingController(recordingService, templateService))
                .build()
    }

    @Test
    fun `startRecording starts recording and returns result`() {
        whenever(recordingService.startRecording(START_RECORDING_FIXTURE_1))
            .thenReturn(RECORDING_FIXTURE_WITH_ID_1)

        whenever(templateService.verifyApiKey(START_RECORDING_FIXTURE_1.templateId, START_RECORDING_FIXTURE_1.apiKey))
            .thenReturn(true)

        mockMvc
            .post("/api/v1/recordings") {
                contentType = APPLICATION_JSON
                content = jacksonObjectMapper().writeValueAsString(START_RECORDING_FIXTURE_1)
            }
            .andExpect {
                accept(APPLICATION_JSON)
                content {
                    jsonPath("$.id") { value(RECORDING_FIXTURE_WITH_ID_1.id) }
                    jsonPath("$.secret") { value(RECORDING_FIXTURE_WITH_ID_1.secret) }
                }
            }
    }

    @Test
    fun `startRecording does not start recording if api key does not match`() {
        whenever(templateService.verifyApiKey(START_RECORDING_FIXTURE_1.templateId, START_RECORDING_FIXTURE_1.apiKey))
            .thenReturn(false)

        mockMvc
            .post("/api/v1/recordings") {
                contentType = APPLICATION_JSON
                content = jacksonObjectMapper().writeValueAsString(START_RECORDING_FIXTURE_1)
            }
            .andExpect { status { isUnauthorized() } }

        verify(recordingService, times(0)).startRecording(any())
    }

    @Test
    fun `cancelRecording cancels recording if secret is correct`() {
        whenever(recordingService.verifySecret(RECORDING_FIXTURE_WITH_ID_1.id, RECORDING_FIXTURE_WITH_ID_1.secret))
            .thenReturn(true)

        mockMvc
            .post("/api/v1/recordings/${RECORDING_FIXTURE_WITH_ID_1.id}/cancel") {
                contentType = APPLICATION_JSON
                content =
                    jacksonObjectMapper().writeValueAsString(
                        CancelRecordingInput(secret = RECORDING_FIXTURE_WITH_ID_1.secret),
                    )
            }
            .andExpect {
                accept(APPLICATION_JSON)
                content {
                    status { isOk() }
                }
            }

        verify(recordingService)
            .cancelRecording(RECORDING_FIXTURE_WITH_ID_1.id)
    }

    @Test
    fun `cancelRecording does not cancel recording if secret is not correct`() {
        whenever(recordingService.verifySecret(RECORDING_FIXTURE_WITH_ID_1.id, RECORDING_FIXTURE_WITH_ID_1.secret))
            .thenReturn(false)

        mockMvc
            .post("/api/v1/recordings/${RECORDING_FIXTURE_WITH_ID_1.id}/cancel") {
                contentType = APPLICATION_JSON
                content =
                    jacksonObjectMapper().writeValueAsString(
                        CancelRecordingInput(secret = RECORDING_FIXTURE_WITH_ID_1.secret),
                    )
            }
            .andExpect {
                accept(APPLICATION_JSON)
                content {
                    status { isUnauthorized() }
                }
            }

        verify(recordingService, times(0))
            .cancelRecording(any())
    }

    @Test
    fun `finishRecording finishes recording if secret is correct`() {
        whenever(recordingService.verifySecret(RECORDING_FIXTURE_WITH_ID_1.id, RECORDING_FIXTURE_WITH_ID_1.secret))
            .thenReturn(true)

        mockMvc
            .post("/api/v1/recordings/${RECORDING_FIXTURE_WITH_ID_1.id}/finish") {
                contentType = APPLICATION_JSON
                content =
                    jacksonObjectMapper().writeValueAsString(
                        FinishRecordingInput(
                            secret = RECORDING_FIXTURE_WITH_ID_1.secret,
                            recording = RECORDING_ENTITY_FIXTURE_WITH_ID_1.recording,
                        ),
                    )
            }
            .andExpect {
                accept(APPLICATION_JSON)
                content {
                    status { isOk() }
                }
            }

        verify(recordingService)
            .finishRecording(
                RECORDING_FIXTURE_WITH_ID_1.id,
                RECORDING_FIXTURE_WITH_ID_1.recording,
            )
    }

    @Test
    fun `finishRecording does not finish recording if secret is not correct`() {
        whenever(recordingService.verifySecret(RECORDING_FIXTURE_WITH_ID_1.id, RECORDING_FIXTURE_WITH_ID_1.secret))
            .thenReturn(false)

        mockMvc
            .post("/api/v1/recordings/${RECORDING_FIXTURE_WITH_ID_1.id}/cancel") {
                contentType = APPLICATION_JSON
                content =
                    jacksonObjectMapper().writeValueAsString(
                        FinishRecordingInput(
                            secret = RECORDING_FIXTURE_WITH_ID_1.secret,
                            recording = RECORDING_FIXTURE_WITH_ID_1.recording,
                        ),
                    )
            }
            .andExpect {
                accept(APPLICATION_JSON)
                content {
                    status { isUnauthorized() }
                }
            }

        verify(recordingService, times(0))
            .finishRecording(RECORDING_FIXTURE_WITH_ID_1.id, RECORDING_FIXTURE_WITH_ID_1.recording)
    }
}
