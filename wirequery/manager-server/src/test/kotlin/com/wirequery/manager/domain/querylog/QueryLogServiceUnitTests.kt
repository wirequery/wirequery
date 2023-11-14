// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import com.wirequery.manager.domain.access.AccessService
import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum
import com.wirequery.manager.domain.query.QueryReport
import com.wirequery.manager.domain.querylog.QueryLogFixtures.CREATE_QUERY_LOG_FIXTURE_1
import com.wirequery.manager.domain.querylog.QueryLogFixtures.QUERY_LOG_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.querylog.QueryLogFixtures.QUERY_LOG_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.querylog.QueryLogFixtures.QUERY_LOG_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQuery
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.STORED_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import java.time.OffsetDateTime
import java.util.*

// TODO improve / increase test coverage
@ExtendWith(MockitoExtension::class)
internal class QueryLogServiceUnitTests {
    @Mock
    private lateinit var queryLogAggregationService: QueryLogAggregationService

    @Mock
    private lateinit var queryLogRepository: QueryLogRepository

    @Mock
    private lateinit var storedQueryService: StoredQueryService

    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var accessService: AccessService

    @Mock
    private lateinit var jdbcTemplate: JdbcTemplate

    @InjectMocks
    private lateinit var queryLogService: QueryLogService

    @Test
    fun `findMainLogsByStoredQueryIds returns the contents of the corresponding repository call, aggregated`() {
        whenever(queryLogAggregationService.toAggregatedDomainObjects(listOf(QUERY_LOG_FIXTURE_WITH_ID_1)))
            .thenReturn(listOf(QUERY_LOG_FIXTURE_WITH_ID_1.copy(message = "some-aggregated")))

        whenever(queryLogRepository.findMainLogsByStoredQueryIds(listOf(1)))
            .thenReturn(listOf(QUERY_LOG_ENTITY_FIXTURE_WITH_ID_1))

        val actual = queryLogService.findMainLogsByStoredQueryIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(QUERY_LOG_FIXTURE_WITH_ID_1.copy(message = "some-aggregated")))
    }

    @Test
    fun `findMainLogs returns the contents of the corresponding repository call, aggregated`() {
        whenever(queryLogAggregationService.toAggregatedDomainObjects(listOf(QUERY_LOG_FIXTURE_WITH_ID_1)))
            .thenReturn(listOf(QUERY_LOG_FIXTURE_WITH_ID_1.copy(message = "some-aggregated")))

        whenever(queryLogRepository.findMainLogsByStoredQueryIds(listOf(1)))
            .thenReturn(listOf(QUERY_LOG_ENTITY_FIXTURE_WITH_ID_1))

        val actual = queryLogService.findMainLogs(QueryLogService.QueryLogFilterInput(storedQueryId = 1))

        assertThat(actual).isEqualTo(listOf(QUERY_LOG_FIXTURE_WITH_ID_1.copy(message = "some-aggregated")))
    }

    @Test
    fun `findByTraceId returns logs by trace id filtered by allowed`() {
        val storedQueryId = 1
        val applicationId = 2

        val queryLogFixture = QUERY_LOG_FIXTURE_WITH_ID_1.copy(storedQueryId = storedQueryId)

        whenever(queryLogAggregationService.toAggregatedDomainObjects(listOf(queryLogFixture)))
            .thenReturn(listOf(QUERY_LOG_FIXTURE_WITH_ID_1.copy(storedQueryId = storedQueryId, message = "aggregated")))

        whenever(queryLogRepository.findNonMainByStoredQueryIdAndTraceId(storedQueryId, "trace-id"))
            .thenReturn(listOf(QUERY_LOG_ENTITY_FIXTURE_WITH_ID_1.copy(storedQueryId = storedQueryId)))

        whenever(storedQueryService.findByIds(listOf(storedQueryId)))
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1.copy(id = storedQueryId, applicationId = applicationId)))

        whenever(accessService.whichAuthorisedByApplicationId(setOf(applicationId), GroupAuthorisationEnum.VIEW_STORED_QUERY))
            .thenReturn(setOf(applicationId))

        val actual = queryLogService.findByTraceId(1, "trace-id")

        assertThat(actual)
            .isEqualTo(listOf(QUERY_LOG_FIXTURE_WITH_ID_1.copy(storedQueryId = storedQueryId, message = "aggregated")))
    }

    @Test
    fun `logOrDisableIfLimitsReached calls save on jdbcTemplate if all requirements are met and publishes an event`() {
        whenever(storedQueryService.findById(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId))
            .thenReturn(STORED_QUERY_FIXTURE_WITH_ID_1.copy(queryLimit = 100, endDate = null))

        whenever(jdbcTemplate.queryForObject(any(), eq(Int::class.java), any()))
            .thenReturn(1)

        val actual = queryLogService.logOrDisableIfLimitsReached(CREATE_QUERY_LOG_FIXTURE_1)

        val dateMock = mock<OffsetDateTime>()

        assertThat(actual!!.copy(createdAt = dateMock))
            .isEqualTo(QUERY_LOG_FIXTURE_WITH_ID_1.copy(createdAt = dateMock))

        verify(publisher).publishEvent(any<QueryLogEvent.QueryLogsCreatedEvent>())

        verify(jdbcTemplate)
            .update(any<PreparedStatementCreator>())

        verify(storedQueryService, times(0))
            .disableQueryById(anyInt())
    }

    @Test
    fun `logOrDisableIfLimitsReached disables query if limits are reached`() {
        whenever(storedQueryService.findById(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId))
            .thenReturn(
                STORED_QUERY_FIXTURE_WITH_ID_1.copy(
                    id = QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                    queryLimit = 0,
                    endDate = null,
                ),
            )

        whenever(storedQueryService.isQuarantined(any<StoredQuery>()))
            .thenReturn(false)

        whenever(jdbcTemplate.queryForObject(any(), eq(Int::class.java), any()))
            .thenReturn(1)

        val actual = queryLogService.logOrDisableIfLimitsReached(CREATE_QUERY_LOG_FIXTURE_1)
        assertThat(actual).isEqualTo(null)

        verify(publisher, times(0))
            .publishEvent(any<QueryLogEvent.QueryLogsCreatedEvent>())

        verify(jdbcTemplate, times(0))
            .update(any<PreparedStatementCreator>())

        verify(storedQueryService)
            .disableQueryById(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)
    }

    @Test
    fun `logOrDisableIfLimitsReached returns null when related objects cannot be found and publishes no events`() {
        assertThat(
            queryLogService.logOrDisableIfLimitsReached(CREATE_QUERY_LOG_FIXTURE_1),
        ).isEqualTo(null)

        verify(queryLogRepository, times(0))
            .save(QUERY_LOG_ENTITY_FIXTURE_1)

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `putTrace calls save on jdbcTemplate if all requirements are met and publishes an event`() {
        whenever(storedQueryService.findById(any()))
            .thenReturn(STORED_QUERY_FIXTURE_WITH_ID_1.copy(queryLimit = 100, endDate = null))

        whenever(storedQueryService.isQuarantined(10))
            .thenReturn(false)

        val actual =
            queryLogService.putTrace(
                QueryReport(
                    appName = "app",
                    queryId = "stored_query:1:trace",
                    message = "message",
                    startTime = 10L,
                    endTime = 20L,
                    traceId = "abc",
                ),
            )

        val dateMock = mock<OffsetDateTime>()

        assertThat(actual!!.copy(createdAt = dateMock))
            .isEqualTo(
                QueryLog(
                    storedQueryId = 1,
                    message = "message",
                    startTime = 10,
                    endTime = 20,
                    appName = "app",
                    traceId = "abc",
                    createdAt = dateMock,
                ),
            )

        verify(publisher).publishEvent(any<QueryLogEvent.QueryLogsCreatedEvent>())

        verify(jdbcTemplate)
            .update(any<PreparedStatementCreator>())

        verify(storedQueryService, times(0))
            .disableQueryById(anyInt())
    }
}
