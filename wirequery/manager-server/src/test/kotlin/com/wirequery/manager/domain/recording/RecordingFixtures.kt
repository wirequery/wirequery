// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import com.wirequery.manager.domain.recording.Recording.StatusEnum.ACTIVE
import com.wirequery.manager.domain.recording.RecordingService.Companion.LOOKBACK_SECS_PLACEHOLDER
import com.wirequery.manager.domain.recording.RecordingService.Companion.RECORDING_TIMEOUT
import com.wirequery.manager.domain.recording.RecordingService.StartRecordingInput
import com.wirequery.manager.domain.recording.RecordingService.UpdateRecordingInput
import java.time.LocalDateTime
import java.time.ZoneId

object RecordingFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    private val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val RECORDING_FIXTURE_WITH_ID_1 =
        Recording(
            id = 1,
            sessionId = 10,
            templateId = 10,
            args = mapOf("a" to "b"),
            secret = "Some secret",
            lookBackSecs = LOOKBACK_SECS_PLACEHOLDER,
            timeoutSecs = RECORDING_TIMEOUT,
            recording = "",
            status = ACTIVE,
            createdAt = OFFSET_DATE_TIME_FIXTURE,
        )

    val RECORDING_ENTITY_FIXTURE_1 =
        RecordingEntity(
            id = null,
            sessionId = 10,
            templateId = 10,
            args = "{\"a\":\"b\"}",
            secret = "Some secret",
            lookBackSecs = LOOKBACK_SECS_PLACEHOLDER,
            timeoutSecs = RECORDING_TIMEOUT,
            recording = "",
            status = ACTIVE,
        )

    val START_RECORDING_FIXTURE_1 =
        StartRecordingInput(
            templateId = 10,
            args = mapOf("a" to "b"),
        )

    val UPDATE_RECORDING_FIXTURE_1 =
        UpdateRecordingInput(
            sessionId = 10,
            templateId = 10,
            args = mapOf("a" to "b"),
            secret = "Some secret",
            lookBackSecs = LOOKBACK_SECS_PLACEHOLDER,
            timeoutSecs = RECORDING_TIMEOUT,
            recording = "",
            status = ACTIVE,
        )

    val RECORDING_ENTITY_FIXTURE_WITH_ID_1 =
        RECORDING_ENTITY_FIXTURE_1.copy(
            id = 1,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
