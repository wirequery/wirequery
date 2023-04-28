package com.wirequery.spring6

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@ExtendWith(MockitoExtension::class)
internal class QueryReportingFilterTest {

    @Mock
    private lateinit var queryReporter: QueryReporter

    @InjectMocks
    private lateinit var queryReportingFilter: QueryReportingFilter

    @Test
    fun `the request and response are intercepted and sent to the query reporter`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val filterChain = mock<FilterChain>()

        queryReportingFilter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(any(), any())
        verify(queryReporter).processInterceptedTraffic(any(), any())
    }

}
