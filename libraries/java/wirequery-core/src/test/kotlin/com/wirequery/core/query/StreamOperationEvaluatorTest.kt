package com.wirequery.core.query

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.wirequery.core.query.context.CompiledQuery.CompiledOperation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.projectnessie.cel.tools.Script

@ExtendWith(MockitoExtension::class)
internal class StreamOperationEvaluatorTest {

    @InjectMocks
    private lateinit var streamOperationEvaluator: StreamOperationEvaluator

    @Test
    fun `map returns a single item of the value`() {
        val scriptMock = mock<Script>()
        whenever(scriptMock.execute(Any::class.java, mapOf()))
            .thenReturn("result")
        val result = streamOperationEvaluator.evaluate(CompiledOperation("map", scriptMock), mapOf())

        assertThat(result).isEqualTo(listOf("result"))
    }

    @Test
    fun `flatMap returns all items of the value for iterables`() {
        val scriptMock = mock<Script>()
        whenever(scriptMock.execute(Any::class.java, mapOf()))
            .thenReturn(listOf("a", "b"))
        val result = streamOperationEvaluator.evaluate(CompiledOperation("flatMap", scriptMock), mapOf())

        assertThat(result).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun `flatMap returns all items of the value for arrays`() {
        val scriptMock = mock<Script>()
        whenever(scriptMock.execute(Any::class.java, mapOf()))
            .thenReturn(arrayOf("a", "b"))
        val result = streamOperationEvaluator.evaluate(CompiledOperation("flatMap", scriptMock), mapOf())

        assertThat(result).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun `flatMap returns all items of the value for arrayNodes`() {
        val scriptMock = mock<Script>()
        whenever(scriptMock.execute(Any::class.java, mapOf()))
            .thenReturn(JsonNodeFactory.instance.arrayNode().also { it.addAll(
                listOf(
                    JsonNodeFactory.instance.textNode("a"),
                    JsonNodeFactory.instance.textNode("b"),
                )) })
        val result = streamOperationEvaluator.evaluate(CompiledOperation("flatMap", scriptMock), mapOf())

        assertThat(result).isEqualTo(listOf(
            JsonNodeFactory.instance.textNode("a"),
            JsonNodeFactory.instance.textNode("b"),
        ))
    }

    @Test
    fun `flatMap throws error when it cannot flatten the result`() {
        val scriptMock = mock<Script>()
        whenever(scriptMock.execute(Any::class.java, mapOf()))
            .thenReturn("a")
        val result = assertThrows<RuntimeException> {
            streamOperationEvaluator.evaluate(CompiledOperation("flatMap", scriptMock), mapOf())
        }
        assertThat(result.message).isEqualTo("Unable to flatten flatMap result")
    }

    @Test
    fun `filter returns the input as a single item if the result is true`() {
        val scriptMock = mock<Script>()
        whenever(scriptMock.execute(Any::class.java, mapOf("it" to "abc")))
            .thenReturn(true)
        val result = streamOperationEvaluator.evaluate(CompiledOperation("filter", scriptMock), mapOf("it" to "abc"))

        assertThat(result).isEqualTo(listOf("abc"))
    }

    @Test
    fun `filter returns empty if the result is false`() {
        val scriptMock = mock<Script>()
        whenever(scriptMock.execute(Any::class.java, mapOf("it" to "abc")))
            .thenReturn(false)
        val result = streamOperationEvaluator.evaluate(CompiledOperation("filter", scriptMock), mapOf("it" to "abc"))

        assertThat(result).isEqualTo(emptyList<Any>())
    }

    @Test
    fun `filter throws exception for non-boolean return value`() {
        val scriptMock = mock<Script>()
        whenever(scriptMock.execute(Any::class.java, mapOf("it" to "abc")))
            .thenReturn(5)
        val result = assertThrows<RuntimeException> {
            streamOperationEvaluator.evaluate(CompiledOperation("filter", scriptMock), mapOf("it" to "abc"))
        }
        assertThat(result.message).isEqualTo("Return value of filter expression is not a boolean")
    }

    @Test
    fun `unknown operation throws error`() {
        val scriptMock = mock<Script>()
        val result = assertThrows<RuntimeException> {
            streamOperationEvaluator.evaluate(CompiledOperation("slim", scriptMock), mapOf())
        }
        assertThat(result.message).isEqualTo("Unknown operation")
    }

    @Test
    fun `no celExpression throws error`() {
        val result = assertThrows<RuntimeException> {
            streamOperationEvaluator.evaluate(CompiledOperation("slim", null), mapOf())
        }
        assertThat(result.message).isEqualTo("No expression provided")
    }
}
