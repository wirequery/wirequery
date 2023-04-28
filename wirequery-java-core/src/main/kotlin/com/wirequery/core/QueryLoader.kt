package com.wirequery.core

interface QueryLoader {
    /**
     * Returns all queries to be executed over a request
     */
    fun getQueries(): List<TraceableQuery>
}

