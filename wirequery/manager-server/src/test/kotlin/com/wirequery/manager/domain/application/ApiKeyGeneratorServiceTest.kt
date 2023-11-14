// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.application

import com.wirequery.manager.domain.query.QueryIdGenerator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ApiKeyGeneratorServiceTest {
    @Test
    fun `api keys are always unique`() {
        val queryIdGenerator = QueryIdGenerator()
        Assertions.assertThat(queryIdGenerator.generateId()).isNotEqualTo(queryIdGenerator.generateId())
    }
}
