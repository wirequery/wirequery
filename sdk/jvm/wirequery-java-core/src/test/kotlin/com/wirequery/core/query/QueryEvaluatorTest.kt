package com.wirequery.core.query

import com.wirequery.core.query.context.QueryHead
import com.wirequery.core.query.context.CompiledQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
internal class QueryEvaluatorTest {
    @Mock
    private lateinit var appHeadEvaluator: AppHeadEvaluator
    @Mock
    private lateinit var streamOperationEvaluator: StreamOperationEvaluator
    @Mock
    private lateinit var aggregatorOperationEvaluator: AggregatorOperationEvaluator
    @Mock
    private lateinit var contextMapCreator: ContextMapCreator
    @InjectMocks
    private lateinit var queryEvaluator: QueryEvaluator

    @Test
    fun `if the app head evaluator matches and there are no operators, return 'it'`() {
        whenever(contextMapCreator.createMaskedContextMap(any(), any()))
            .thenReturn(SOME_CONTEXT_MAP)

        whenever(appHeadEvaluator.evaluate(SOME_COMPILED_QUERY.queryHead, "GET", "/abc", 200))
            .thenReturn(AppHeadEvaluator.AppHeadEvaluationResult(true, mapOf("a" to "b")))

        val actual = queryEvaluator.evaluate(SOME_COMPILED_QUERY, SOME_INTERCEPTED)
        val expected = listOf(SOME_CONTEXT_MAP)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `if the app head evaluator does not match, return nothing`() {
        whenever(appHeadEvaluator.evaluate(SOME_COMPILED_QUERY.queryHead, "GET", "/abc", 200))
            .thenReturn(AppHeadEvaluator.AppHeadEvaluationResult(false, mapOf()))

        val actual = queryEvaluator.evaluate(SOME_COMPILED_QUERY, SOME_INTERCEPTED)
        assertThat(actual).isEqualTo(emptyList<Any>())
    }

    @Test
    fun `'it' and 'context' are passed to the first operation`() {
        whenever(contextMapCreator.createMaskedContextMap(any(), any()))
            .thenReturn(SOME_CONTEXT_MAP)
        whenever(appHeadEvaluator.evaluate(SOME_COMPILED_QUERY.queryHead, "GET", "/abc", 200))
            .thenReturn(AppHeadEvaluator.AppHeadEvaluationResult(true, mapOf()))

        val operationMock = mock<CompiledQuery.CompiledOperation>()
        val operationMockResult = mock<CompiledQuery.CompiledOperation>()
        val query = SOME_COMPILED_QUERY.copy(streamOperations = listOf(operationMock))

        whenever(streamOperationEvaluator.evaluate(any(), any()))
            .thenReturn(listOf(operationMockResult))

        val actual = queryEvaluator.evaluate(query, SOME_INTERCEPTED)
        assertThat(actual).isEqualTo(listOf(operationMockResult))

        verify(streamOperationEvaluator).evaluate(operationMock, mapOf("it" to SOME_CONTEXT_MAP, "context" to SOME_CONTEXT_MAP))
    }

    @Test
    fun `the result of one operation is passed into the next through 'it'`() {
        whenever(contextMapCreator.createMaskedContextMap(any(), any()))
            .thenReturn(SOME_CONTEXT_MAP)
        whenever(appHeadEvaluator.evaluate(SOME_COMPILED_QUERY.queryHead, "GET", "/abc", 200))
            .thenReturn(AppHeadEvaluator.AppHeadEvaluationResult(true, mapOf()))

        val operationMock = mock<CompiledQuery.CompiledOperation>()
        val operationMockResult = mock<CompiledQuery.CompiledOperation>()
        val query = SOME_COMPILED_QUERY.copy(streamOperations = listOf(operationMock, operationMock))

        whenever(streamOperationEvaluator.evaluate(any(), any()))
            .thenReturn(listOf(operationMockResult))

        val actual = queryEvaluator.evaluate(query, SOME_INTERCEPTED)
        assertThat(actual).isEqualTo(listOf(operationMockResult))

        verify(streamOperationEvaluator)
            .evaluate(operationMock, mapOf("it" to SOME_CONTEXT_MAP, "context" to SOME_CONTEXT_MAP))

        verify(streamOperationEvaluator)
            .evaluate(operationMock, mapOf("it" to operationMockResult, "context" to SOME_CONTEXT_MAP))

        verify(aggregatorOperationEvaluator, times(0))
            .evaluate(any(), any())
    }

    @Test
    fun `the result of all operations is passed into the aggregator operation evaluator`() {
        whenever(contextMapCreator.createMaskedContextMap(any(), any()))
            .thenReturn(SOME_CONTEXT_MAP)
        whenever(appHeadEvaluator.evaluate(SOME_COMPILED_QUERY.queryHead, "GET", "/abc", 200))
            .thenReturn(AppHeadEvaluator.AppHeadEvaluationResult(true, mapOf()))

        val operationMock = mock<CompiledQuery.CompiledOperation>()
        val operationMockResult = mock<CompiledQuery.CompiledOperation>()
        val operationMockResult2 = mock<CompiledQuery.CompiledOperation>()
        val query = SOME_COMPILED_QUERY.copy(
            streamOperations = listOf(operationMock),
            aggregatorOperation = operationMock
        )

        whenever(streamOperationEvaluator.evaluate(any(), any()))
            .thenReturn(listOf(operationMockResult))

        whenever(aggregatorOperationEvaluator.evaluate(any(), any()))
            .thenReturn(listOf(operationMockResult2))

        val actual = queryEvaluator.evaluate(query, SOME_INTERCEPTED)
        assertThat(actual).isEqualTo(listOf(operationMockResult2))

        verify(streamOperationEvaluator)
            .evaluate(operationMock, mapOf("it" to SOME_CONTEXT_MAP, "context" to SOME_CONTEXT_MAP))

        verify(aggregatorOperationEvaluator)
            .evaluate(query, mapOf("it" to operationMockResult, "context" to SOME_CONTEXT_MAP))
    }

    private companion object {
        val SOME_COMPILED_QUERY = CompiledQuery(
            queryHead = QueryHead(
                method = "GET",
                path = "/abc",
                statusCode = "2xx"
            ),
            streamOperations = listOf(),
            aggregatorOperation = null
        )

        val SOME_INTERCEPTED = QueryEvaluator.InterceptedRequestResponse(
            method = "GET",
            path = "/abc",
            statusCode = 200,
            queryParameters = mapOf("a" to listOf("b")),
            requestBody = "",
            responseBody = "",
            requestHeaders = mapOf(),
            responseHeaders = mapOf(),
            extensions = mapOf()
        )

        val SOME_CONTEXT_MAP = mock<Map<String, Any>>()
    }
}
