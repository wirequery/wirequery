package com.wirequery.manager.domain.querylog

import java.time.LocalDateTime
import java.time.ZoneId

object QueryLogFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    private val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val QUERY_LOG_FIXTURE_WITH_ID_1 =
        QueryLog(
            storedQueryId = 10,
            message = "Some message",
            createdAt = OFFSET_DATE_TIME_FIXTURE,
            startTime = 10L,
            endTime = 20L,
            traceId = "abc",
            appName = "SomeName",
        )

    val QUERY_LOG_ENTITY_FIXTURE_1 =
        QueryLogEntity(
            storedQueryId = 10,
            message = "Some message",
            startTime = 10L,
            endTime = 20L,
            traceId = "abc",
            appName = "SomeName",
            main = true,
        )

    val CREATE_QUERY_LOG_FIXTURE_1 =
        QueryLogService.CreateQueryLogInput(
            storedQueryId = 10,
            message = "Some message",
            startTime = 10L,
            endTime = 20L,
            traceId = "abc",
            appName = "SomeName",
        )

    val QUERY_LOG_ENTITY_FIXTURE_WITH_ID_1 =
        QUERY_LOG_ENTITY_FIXTURE_1.copy(
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
