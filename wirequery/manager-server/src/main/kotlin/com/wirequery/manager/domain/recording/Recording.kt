package com.wirequery.manager.domain.recording

import java.time.OffsetDateTime

data class Recording(
    val id: Int,
    val sessionId: Int,
    val templateId: Int?,
    val args: Map<String, String>,
    val secret: String,
    val lookBackSecs: Int,
    val timeoutSecs: Int,
    val recording: String,
    val status: StatusEnum,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null,
) {
    enum class StatusEnum {
        ACTIVE,
        CANCELLED,
        FINISHED,
    }
}
