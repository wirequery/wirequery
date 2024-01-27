// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query

import com.wirequery.core.query.context.CompiledQuery
import com.wirequery.core.query.context.Query
import com.wirequery.core.query.context.QueryHead
import dev.cel.runtime.CelRuntime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class QueryCompilerTest {
    @Mock
    private lateinit var expressionCompiler: ExpressionCompiler

    @Mock
    private lateinit var queryAuthorizer: QueryAuthorizer

    @InjectMocks
    private lateinit var queryCompiler: QueryCompiler

    @Test
    fun `queries are only compiled for authorized resources`() {
        whenever(queryAuthorizer.isAuthorized("GET", "/abc"))
            .thenReturn(false)

        val query =
            Query(
                queryHead =
                    QueryHead(
                        method = "GET",
                        path = "/abc",
                        statusCode = "123",
                    ),
                streamOperations = listOf(),
                aggregatorOperation = null,
            )

        val exception =
            assertThrows<IllegalStateException> {
                queryCompiler.compile(query)
            }

        assertThat(exception.message)
            .isEqualTo("Query not authorized for compilation.")
    }

    @Test
    fun `uncompiled queries are compiled for non-compilable queries`() {
        whenever(queryAuthorizer.isAuthorized(any(), any()))
            .thenReturn(true)

        val query =
            Query(
                queryHead =
                    QueryHead(
                        method = "GET",
                        path = "/abc",
                        statusCode = "123",
                    ),
                streamOperations = listOf(),
                aggregatorOperation = null,
            )
        val expected =
            CompiledQuery(
                QueryHead(
                    method = "GET",
                    path = "/abc",
                    statusCode = "123",
                ),
                streamOperations = listOf(),
                aggregatorOperation = null,
            )
        val actual = queryCompiler.compile(query)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `stream operations are compiled`() {
        whenever(queryAuthorizer.isAuthorized(any(), any()))
            .thenReturn(true)

        val programMock = mock<CelRuntime.Program>()
        whenever(expressionCompiler.compile("1 + 1"))
            .thenReturn(programMock)

        val query =
            Query(
                queryHead = SOME_APP_HEAD,
                streamOperations =
                    listOf(
                        Query.Operation("map", "1 + 1"),
                    ),
                aggregatorOperation = null,
            )

        val actual = queryCompiler.compile(query).streamOperations.single()

        assertThat(actual.name).isEqualTo("map")
        assertThat(actual.celExpression).isEqualTo(programMock)
    }

    @Test
    fun `aggregation operations are compiled`() {
        whenever(queryAuthorizer.isAuthorized(any(), any()))
            .thenReturn(true)

        val programMock = mock<CelRuntime.Program>()

        whenever(expressionCompiler.compile("1 + 1"))
            .thenReturn(programMock)

        val query =
            Query(
                queryHead = SOME_APP_HEAD,
                streamOperations = listOf(),
                aggregatorOperation = Query.Operation("distinctBy", "1 + 1"),
            )

        val actual = queryCompiler.compile(query).aggregatorOperation

        assertThat(actual!!.name).isEqualTo("distinctBy")
        assertThat(actual.celExpression).isEqualTo(programMock)
    }

    private companion object {
        val SOME_APP_HEAD =
            QueryHead(
                method = "GET",
                path = "/abc",
                statusCode = "123",
            )
    }
}
