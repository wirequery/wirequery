package com.wirequery.manager.domain.application

import com.wirequery.manager.domain.query.QueryIdGenerator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ApiKeyGeneratorServiceTest {
    @Test
    fun `api keys are always unique`() {
        val queryIdGenerator = QueryIdGenerator()
        Assertions.assertThat(queryIdGenerator.generateId()).isNotEqualTo(queryIdGenerator.generateId())
    }
}
