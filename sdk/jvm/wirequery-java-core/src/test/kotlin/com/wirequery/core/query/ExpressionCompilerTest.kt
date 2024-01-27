// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query

import dev.cel.common.CelValidationException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ExpressionCompilerTest {
    private val expressionCompiler: ExpressionCompiler = ExpressionCompiler()

    @Test
    fun `valid compiled expressions can be run`() {
        assertThat(
            expressionCompiler.compile("1 + 1")
                .eval(mapOf<String, Any>()),
        ).isEqualTo(2L)
        assertThat(
            expressionCompiler.compile("it")
                .eval(mapOf("it" to "some value")),
        ).isEqualTo("some value")
        assertThat(
            expressionCompiler.compile("context")
                .eval(mapOf("context" to "some value")),
        ).isEqualTo("some value")
    }

    @Test
    fun `invalid compiled expressions cannot be created`() {
        assertThrows<CelValidationException> {
            expressionCompiler.compile("that")
        }
    }
}
