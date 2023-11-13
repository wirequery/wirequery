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
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
)
