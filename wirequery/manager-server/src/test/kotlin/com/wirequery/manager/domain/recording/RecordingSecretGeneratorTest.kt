// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

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
