// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import org.springframework.stereotype.Service
import java.util.*

@Service
class RecordingSecretGenerator {
    fun generate() = UUID.randomUUID().toString()
}
