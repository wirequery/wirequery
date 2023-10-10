package com.wirequery.spring6

import com.wirequery.core.query.QueryEvaluator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class TraceCacheTest {

    @Test
    fun `store saves by trace id if set, and can be retrieved with findByTraceId`() {
        val traceId = "abc"
        val traceCache = TraceCache()
        val interceptedRequestResponse = mock<QueryEvaluator.InterceptedRequestResponse>()
        whenever(interceptedRequestResponse.traceId)
            .thenReturn(traceId)

        traceCache.store(interceptedRequestResponse)
        val actual = traceCache.findByTraceId(traceId)

        assertThat(actual).isEqualTo(interceptedRequestResponse)
    }

}
