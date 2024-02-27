// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring5

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class QueryFilter(
    private val interceptedQueryTrafficProcessor: InterceptedQueryTrafficProcessor,
    private val requestData: RequestData,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        requestData.startTime = System.currentTimeMillis()
        filterChain.doFilter(wrappedRequest, wrappedResponse)
        wrappedResponse.copyBodyToResponse()

        interceptedQueryTrafficProcessor.processInterceptedTraffic(wrappedRequest, wrappedResponse)
    }
}
