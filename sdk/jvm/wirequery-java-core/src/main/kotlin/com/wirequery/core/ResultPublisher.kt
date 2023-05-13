package com.wirequery.core

interface ResultPublisher {
    /**
     * Publish the error of executing the provided query
     */
    fun publishError(queryId: String, message: String)

    /**
     * Publish the results of executing the provided query
     */
    fun publishResult(query: TraceableQuery, results: Any)
}
