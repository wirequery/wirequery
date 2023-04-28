package com.wirequery.core.query

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.projectnessie.cel.tools.ScriptCreateException

internal class ExpressionCompilerTest {
    private val expressionCompiler: ExpressionCompiler = ExpressionCompiler()

    @Test
    fun `valid compiled expressions can be run`() {
        assertThat(expressionCompiler.compile("1 + 1")
            .execute(Int::class.java, mapOf())).isEqualTo(2)
        assertThat(expressionCompiler.compile("it")
            .execute(String::class.java, mapOf("it" to "some value"))).isEqualTo("some value")
        assertThat(expressionCompiler.compile("context")
            .execute(String::class.java, mapOf("context" to "some value"))).isEqualTo("some value")
    }

    @Test
    fun `invalid compiled expressions cannot be created`() {
        assertThrows<ScriptCreateException> {
            expressionCompiler.compile("that")
        }
    }
}
