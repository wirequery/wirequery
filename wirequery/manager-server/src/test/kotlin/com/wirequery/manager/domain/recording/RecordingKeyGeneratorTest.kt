// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RecordingKeyGeneratorTest {
    @Test
    fun `generateSecret generates secrets that are always unique`() {
        val recordingKeyGenerator = RecordingKeyGenerator()
        assertThat(recordingKeyGenerator.generateSecret())
            .isNotEqualTo(recordingKeyGenerator.generateSecret())
    }

    @Test
    fun `generateCorrelationId generates correlation id that is always unique`() {
        val recordingKeyGenerator = RecordingKeyGenerator()
        assertThat(recordingKeyGenerator.generateCorrelationId())
            .isNotEqualTo(recordingKeyGenerator.generateCorrelationId())
    }
}
