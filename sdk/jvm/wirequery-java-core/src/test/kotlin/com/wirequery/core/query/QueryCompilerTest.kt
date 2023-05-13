package com.wirequery.core.query

import com.wirequery.core.query.context.QueryHead
import com.wirequery.core.query.context.CompiledQuery
import com.wirequery.core.query.context.Query
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.projectnessie.cel.tools.Script

@ExtendWith(MockitoExtension::class)
internal class QueryCompilerTest {

    @Mock
    private lateinit var expressionCompiler: ExpressionCompiler

    @InjectMocks
    private lateinit var queryCompiler: QueryCompiler

    @Test
    fun `uncompiled queries are compiled for non-compilable queries`() {
        val query = Query(
            queryHead = QueryHead(
                method = "GET",
                path = "/abc",
                statusCode = "123"
            ),
            streamOperations = listOf(),
            aggregatorOperation = null
        )
        val expected = CompiledQuery(
            QueryHead(
                method = "GET",
                path = "/abc",
                statusCode = "123"
            ),
            streamOperations = listOf(),
            aggregatorOperation = null
        )
        val actual = queryCompiler.compile(query)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `stream operations are compiled`() {
        val scriptMock = mock<Script>()
        whenever(expressionCompiler.compile("1 + 1"))
            .thenReturn(scriptMock)
        val query = Query(
            queryHead = SOME_APP_HEAD,
            streamOperations = listOf(
                Query.Operation("map", "1 + 1")
            ),
            aggregatorOperation = null
        )
        val actual = queryCompiler.compile(query).streamOperations.single()
        assertThat(actual.name).isEqualTo("map")
        assertThat(actual.celExpression).isEqualTo(scriptMock)
    }

    @Test
    fun `aggregation operations are compiled`() {
        val scriptMock = mock<Script>()
        whenever(expressionCompiler.compile("1 + 1"))
            .thenReturn(scriptMock)
        val query = Query(
            queryHead = SOME_APP_HEAD,
            streamOperations = listOf(),
            aggregatorOperation = Query.Operation("distinctBy", "1 + 1")
        )
        val actual = queryCompiler.compile(query).aggregatorOperation
        assertThat(actual!!.name).isEqualTo("distinctBy")
        assertThat(actual.celExpression).isEqualTo(scriptMock)
    }

    private companion object {
        val SOME_APP_HEAD = QueryHead(
            method = "GET",
            path = "/abc",
            statusCode = "123"
        )
    }
}
