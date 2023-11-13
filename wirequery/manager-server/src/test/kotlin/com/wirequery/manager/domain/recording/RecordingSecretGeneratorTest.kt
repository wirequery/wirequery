package com.wirequery.manager.domain.recording

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RecordingSecretGeneratorTest {
    @Test
    fun `generated secrets are always unique`() {
        val recordingSecretGenerator = RecordingSecretGenerator()
        assertThat(recordingSecretGenerator.generate())
            .isNotEqualTo(recordingSecretGenerator.generate())
    }
}
