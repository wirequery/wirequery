// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring5

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyLong

@ExtendWith(MockitoExtension::class)
internal class QueryFilterTest {

    @Mock
    private lateinit var interceptedQueryTrafficProcessor: InterceptedQueryTrafficProcessor

    @Mock
    private lateinit var requestData: RequestData

    @InjectMocks
    private lateinit var queryFilter: QueryFilter

    @Test
    fun `the request and response are intercepted and sent to the query reporter`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val filterChain = mock<FilterChain>()

        queryFilter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(any(), any())
        verify(interceptedQueryTrafficProcessor).processInterceptedTraffic(any(), any())

        verify(requestData).startTime = anyLong()
    }

}
