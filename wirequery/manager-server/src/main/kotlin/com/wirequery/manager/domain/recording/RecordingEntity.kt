// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import com.wirequery.manager.domain.recording.Recording.StatusEnum
import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("recordings")
data class RecordingEntity(
    @Id
    val id: Int? = null,
    val sessionId: Int,
    val templateId: Int?,
    val args: String,
    val secret: String,
    val lookBackSecs: Int,
    val timeoutSecs: Int,
    val recording: String,
    val status: StatusEnum,
    val correlationId: String,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
)
