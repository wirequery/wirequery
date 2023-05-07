package com.wirequery.spring6

import com.wirequery.core.QueryLoader
import com.wirequery.core.TraceableQuery
import org.springframework.stereotype.Service

@Service
class CachingQueryLoader: QueryLoader {
    override fun getQueries(): List<TraceableQuery> {
        TODO("Not yet implemented")
    }

}
