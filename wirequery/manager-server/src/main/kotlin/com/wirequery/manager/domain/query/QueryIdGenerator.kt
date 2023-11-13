package com.wirequery.manager.domain.query

import org.springframework.stereotype.Service
import java.util.*

@Service
class QueryIdGenerator {
    fun generateId() = UUID.randomUUID().toString()
}
