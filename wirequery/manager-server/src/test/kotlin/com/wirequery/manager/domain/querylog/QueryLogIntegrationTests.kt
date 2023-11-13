package com.wirequery.manager.domain.querylog

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.application.ApplicationFixtures.CREATE_APPLICATION_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.querylog.QueryLogFixtures.CREATE_QUERY_LOG_FIXTURE_1
import com.wirequery.manager.domain.querylog.QueryLogService.QueryLogFilterInput
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.CREATE_STORED_QUERY_FIXTURE_1
import com.wirequery.manager.domain.storedquery.StoredQueryRepository
import com.wirequery.manager.domain.storedquery.StoredQueryService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class QueryLogIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var applicationService: ApplicationService

    @Autowired
    private lateinit var queryLogService: QueryLogService

    @Autowired
    private lateinit var storedQueryService: StoredQueryService

    @Autowired
    private lateinit var storedQueryRepository: StoredQueryRepository

    @Test
    fun `QueryLogs can be created, updated, fetched and deleted`() {
        val application = applicationService.create(CREATE_APPLICATION_FIXTURE_1)
        val storedQuery = storedQueryService.create(CREATE_STORED_QUERY_FIXTURE_1.copy(query = application.name))

        queryLogService.logOrDisableIfLimitsReached(CREATE_QUERY_LOG_FIXTURE_1.copy(storedQueryId = storedQuery.id))

        assertThat(queryLogService.findMainLogs(QueryLogFilterInput(storedQuery.id))).isNotEmpty

        assertThat(queryLogService.findMainLogsByStoredQueryIds(listOf(storedQuery.id))).isNotEmpty
    }
}
