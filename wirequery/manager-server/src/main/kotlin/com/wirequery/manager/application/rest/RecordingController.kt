// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.rest

import com.wirequery.manager.domain.FunctionalException.Companion.functionalError
import com.wirequery.manager.domain.recording.RecordingService
import com.wirequery.manager.domain.recording.RecordingService.StartRecordingInput
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"])
@RequestMapping("/api/v1/recordings")
@RestController
class RecordingController(
    private val recordingService: RecordingService,
) {
    @PostMapping
    fun startRecording(
        @RequestBody input: StartRecordingInput,
    ): PublicRecordingSummary {
        return recordingService.startRecording(input).let {
            PublicRecordingSummary(
                id = it.id,
                secret = it.secret,
            )
        }
    }

    @PostMapping("/{id}/cancel")
    fun cancelRecording(
        @PathVariable id: Int,
        @RequestBody input: CancelRecordingInput,
    ): ResponseEntity<String> {
        if (!recordingService.verifySecret(id, input.secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        recordingService.cancelRecording(id)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{id}/finish")
    fun finishRecording(
        @PathVariable id: Int,
        @RequestBody input: FinishRecordingInput,
    ) {
        if (!recordingService.verifySecret(id, input.secret)) {
            functionalError("Secret incorrect.")
        }
        recordingService.finishRecording(id, input.recording)
    }

    data class CancelRecordingInput(
        val secret: String,
    )

    data class FinishRecordingInput(
        val secret: String,
        val recording: String,
    )

    // We only want to expose the information the customer needs.
    data class PublicRecordingSummary(
        val id: Int,
        val secret: String,
    )
}
