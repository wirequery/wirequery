package com.wirequery.core

interface ResultPublisher {
    /**
     * Publish the results of executing the provided query
     */
    fun publishResult(query: TraceableQuery, results: Any, startTime: Long, endTime: Long, traceId: String?)

    /**
     * Publish the error of executing the provided query
     */
    fun publishError(queryId: String, message: String, startTime: Long, endTime: Long, traceId: String?)
}
