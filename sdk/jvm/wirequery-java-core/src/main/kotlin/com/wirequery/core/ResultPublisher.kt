// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core

interface ResultPublisher {
    /**
     * Publish the results of executing the provided query
     */
    fun publishResult(
        query: TraceableQuery,
        results: Any,
        startTime: Long,
        endTime: Long,
        traceId: String?,
        requestCorrelationId: String?,
    )

    /**
     * Publish the error of executing the provided query
     */
    fun publishError(
        queryId: String,
        message: String,
        startTime: Long,
        endTime: Long,
        traceId: String?,
        requestCorrelationId: String?,
    )
}
