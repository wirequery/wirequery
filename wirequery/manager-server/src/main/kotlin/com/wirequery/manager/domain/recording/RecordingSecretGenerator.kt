package com.wirequery.manager.domain.recording

import org.springframework.stereotype.Service
import java.util.*

@Service
class RecordingSecretGenerator {
    fun generate() = UUID.randomUUID().toString()
}
