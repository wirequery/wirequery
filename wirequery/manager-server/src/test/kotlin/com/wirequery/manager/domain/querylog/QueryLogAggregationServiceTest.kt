// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import com.wirequery.manager.domain.query.AggregatorService
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.query.QueryReport
import com.wirequery.manager.domain.querylog.QueryLogAggregationService.Companion.IRRELEVANT_FOR_AGGREGATOR
import com.wirequery.manager.domain.querylog.QueryLogFixtures.QUERY_LOG_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.STORED_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryService
import com.wirequery.manager.domain.storedquery.StoredQueryService.Companion.STORED_QUERY_PREFIX
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class QueryLogAggregationServiceTest {
    @Mock
    private lateinit var storedQueryService: StoredQueryService

    @Mock
    private lateinit var queryParserService: QueryParserService

    @Mock
    private lateinit var aggregatorService: AggregatorService

    @InjectMocks
    private lateinit var queryLogAggregationService: QueryLogAggregationService

    @Test
    fun `toAggregatedDomainObjects will transform query logs into aggregated query logs, null turns into non entry`() {
        val parsedQuery = mock<QueryParserService.Query>()
        val someAggregator = mock<AggregatorService.Aggregator>()

        whenever(storedQueryService.findByIds(listOf(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1.copy(id = QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))

        whenever(queryParserService.parse(STORED_QUERY_FIXTURE_WITH_ID_1.query))
            .thenReturn(parsedQuery)

        whenever(aggregatorService.create(parsedQuery))
            .thenReturn(someAggregator)

        whenever(
            someAggregator.apply(
                QueryReport(
                    appName = IRRELEVANT_FOR_AGGREGATOR,
                    queryId = STORED_QUERY_PREFIX + QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                    message = QUERY_LOG_FIXTURE_WITH_ID_1.message,
                    startTime = 10L,
                    endTime = 20L,
                    traceId = "abc",
                ),
            ),
        )
            .thenReturn(null)

        val results = queryLogAggregationService.toAggregatedDomainObjects(QUERY_LOGS)

        assertThat(results.size).isEqualTo(0)
    }

    @Test
    fun `toAggregatedDomainObjects will transform query logs into aggregated query logs, non-null is resulted`() {
        val parsedQuery = mock<QueryParserService.Query>()
        val someAggregator = mock<AggregatorService.Aggregator>()
        val someQueryReport = mock<QueryReport>()

        whenever(someQueryReport.message).thenReturn("some-message")

        whenever(storedQueryService.findByIds(listOf(QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1.copy(id = QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId)))

        whenever(queryParserService.parse(STORED_QUERY_FIXTURE_WITH_ID_1.query))
            .thenReturn(parsedQuery)

        whenever(aggregatorService.create(parsedQuery))
            .thenReturn(someAggregator)

        whenever(
            someAggregator.apply(
                QueryReport(
                    appName = IRRELEVANT_FOR_AGGREGATOR,
                    queryId = STORED_QUERY_PREFIX + QUERY_LOG_FIXTURE_WITH_ID_1.storedQueryId,
                    message = QUERY_LOG_FIXTURE_WITH_ID_1.message,
                    startTime = 10L,
                    endTime = 20L,
                    traceId = "abc",
                ),
            ),
        )
            .thenReturn(someQueryReport)

        val results = queryLogAggregationService.toAggregatedDomainObjects(QUERY_LOGS)

        assertThat(results).isEqualTo(listOf(QUERY_LOG_FIXTURE_WITH_ID_1.copy(message = "some-message")))
    }

    private companion object {
        val QUERY_LOGS = listOf(QUERY_LOG_FIXTURE_WITH_ID_1)
    }
}
