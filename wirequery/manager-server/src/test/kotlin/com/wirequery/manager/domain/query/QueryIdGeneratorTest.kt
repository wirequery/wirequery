package com.wirequery.manager.domain.query

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QueryIdGeneratorTest {
    @Test
    fun `ids are always unique`() {
        val queryIdGenerator = QueryIdGenerator()
        assertThat(queryIdGenerator.generateId()).isNotEqualTo(queryIdGenerator.generateId())
    }
}
