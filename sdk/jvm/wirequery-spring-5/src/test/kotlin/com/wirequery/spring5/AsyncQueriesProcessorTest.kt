// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring5

import com.wirequery.core.QueryLoader
import com.wirequery.core.ResultPublisher
import com.wirequery.core.TraceableQuery
import com.wirequery.core.query.QueryEvaluator
import com.wirequery.core.query.context.CompiledQuery
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class AsyncQueriesProcessorTest {
    @Mock
    private lateinit var queryEvaluator: QueryEvaluator

    @Mock
    private lateinit var queryLoader: QueryLoader

    @Mock
    private lateinit var resultPublisher: ResultPublisher

    @InjectMocks
    private lateinit var asyncQueriesProcessor: AsyncQueriesProcessor

    @Test
    fun `execute will get the queries, evaluate them and publish results based on the intercepted traffic`() {
        val intercepted = mock<QueryEvaluator.InterceptedRequestResponse>()
        val compiledQuery = mock<CompiledQuery>()
        val traceableQuery = TraceableQuery(queryId = SOME_QUERY_ID, compiledQuery = compiledQuery)

        whenever(queryLoader.getQueries())
            .thenReturn(listOf(traceableQuery))

        val result = SOME_RESULT

        whenever(queryEvaluator.evaluate(compiledQuery, intercepted))
            .thenReturn(listOf(result))

        asyncQueriesProcessor.execute(intercepted)

        verify(resultPublisher).publishResult(traceableQuery, result, 0, 0, null, null)
    }

    @Test
    fun `execute publish error when an error occurs`() {
        val intercepted = mock<QueryEvaluator.InterceptedRequestResponse>()
        val compiledQuery = mock<CompiledQuery>()
        val traceableQuery = TraceableQuery(queryId = SOME_QUERY_ID, compiledQuery = compiledQuery)

        whenever(queryLoader.getQueries())
            .thenReturn(listOf(traceableQuery))

        val exception = RuntimeException(SOME_ERROR_MESSAGE)

        whenever(queryEvaluator.evaluate(compiledQuery, intercepted))
            .thenThrow(exception)

        asyncQueriesProcessor.execute(intercepted)

        verify(resultPublisher).publishError(SOME_QUERY_ID, SOME_ERROR_MESSAGE, 0, 0, null, null)
    }

    private companion object {
        const val SOME_QUERY_ID = "some-query"
        const val SOME_RESULT = "some-result"
        const val SOME_ERROR_MESSAGE = "some-error"
    }
}
