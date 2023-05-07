package com.wirequery.core

interface ResultPublisher {
    /**
     * Publish the results of executing the provided query
     */
    fun publishResult(query: TraceableQuery, results: Any)
}
