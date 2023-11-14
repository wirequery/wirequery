// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.query

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QueryIdGeneratorTest {
    @Test
    fun `ids are always unique`() {
        val queryIdGenerator = QueryIdGenerator()
        assertThat(queryIdGenerator.generateId()).isNotEqualTo(queryIdGenerator.generateId())
    }
}
