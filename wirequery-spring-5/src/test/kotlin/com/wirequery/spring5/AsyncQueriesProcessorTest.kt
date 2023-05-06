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
        val traceableQuery = TraceableQuery(name = "some-query", compiledQuery = compiledQuery)

        whenever(queryLoader.getQueries())
            .thenReturn(listOf(traceableQuery))

        val result = "some-result"

        whenever(queryEvaluator.evaluate(compiledQuery, intercepted))
            .thenReturn(listOf(result))

        asyncQueriesProcessor.execute(intercepted)

        verify(resultPublisher).publishResult(traceableQuery, result)
    }

}
