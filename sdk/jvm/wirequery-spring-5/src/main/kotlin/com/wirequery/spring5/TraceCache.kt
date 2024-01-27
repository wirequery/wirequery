// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring5

import com.google.common.cache.CacheBuilder
import com.wirequery.core.query.QueryEvaluator.InterceptedRequestResponse
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class TraceCache {
    var cache =
        CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build<String, InterceptedRequestResponse>()

    fun store(intercepted: InterceptedRequestResponse) {
        if (intercepted.traceId == null) {
            return
        }
        cache.put(intercepted.traceId!!, intercepted)
    }

    fun findByTraceId(traceId: String): InterceptedRequestResponse? {
        return cache.getIfPresent(traceId)
    }
}
