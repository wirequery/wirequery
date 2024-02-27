// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import com.wirequery.manager.domain.access.AccessService
import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum.VIEW_QUERY_LOGS
import com.wirequery.manager.domain.query.QueryReport
import com.wirequery.manager.domain.querylog.QueryLogEvent.QueryLogsCreatedEvent
import com.wirequery.manager.domain.querylog.QueryLogEvent.QueryLogsFetchedEvent
import com.wirequery.manager.domain.storedquery.StoredQueryService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
@Transactional
class QueryLogService(
    private val queryLogAggregationService: QueryLogAggregationService,
    private val queryLogRepository: QueryLogRepository,
    private val storedQueryService: StoredQueryService,
    private val publisher: ApplicationEventPublisher,
    private val accessService: AccessService,
    private val jdbcTemplate: JdbcTemplate,
) {
    /** Does not return logs related to tracing. */
    fun findMainLogsByStoredQueryIds(storedQueryIds: Iterable<Int>): List<QueryLog> {
        return queryLogRepository.findMainLogsByStoredQueryIds(storedQueryIds)
            .map(::toDomainObject)
            .let(queryLogAggregationService::toAggregatedDomainObjects)
            .also { publisher.publishEvent(QueryLogsFetchedEvent(this, it)) }
    }

    /** Does not return logs related to tracing. */
    fun findMainLogs(filterInput: QueryLogFilterInput): List<QueryLog> {
        return queryLogRepository.findMainLogsByStoredQueryIds(listOf(filterInput.storedQueryId))
            .map(::toDomainObject)
            .let(queryLogAggregationService::toAggregatedDomainObjects)
            .also { publisher.publishEvent(QueryLogsFetchedEvent(this, it)) }
    }

    /** Only returns logs related to tracing. */
    fun findByTraceId(
        storedQueryId: Int,
        traceId: String,
    ): List<QueryLog> {
        return queryLogRepository.findNonMainByStoredQueryIdAndTraceId(storedQueryId, traceId)
            .map(::toDomainObject)
            .let(queryLogAggregationService::toAggregatedDomainObjects)
            .let(::clearMessagesIfUserHasNoAccess) // TODO test
            .also { publisher.publishEvent(QueryLogsFetchedEvent(this, it)) }
    }

    private fun clearMessagesIfUserHasNoAccess(queryLog: List<QueryLog>): List<QueryLog> {
        val storedQueryIds = queryLog.map { it.storedQueryId }.distinct()
        val storedQuerys = storedQueryService.findByIds(storedQueryIds)

        val allowedApplicationIds =
            accessService.whichAuthorisedByApplicationId(storedQuerys.map { it.applicationId }.toSet(), VIEW_QUERY_LOGS)

        val storedQueryIdIdToApplicationId = storedQuerys.associateBy({ it.id }) { it.applicationId }

        return queryLog.map {
            val appId = storedQueryIdIdToApplicationId[it.storedQueryId]
            if (allowedApplicationIds.contains(appId)) {
                it
            } else {
                it.copy(message = "{\"error\": \"Sorry, you do not have access to this query log.\"}")
            }
        }
    }

    /** Stores as non-tracing log. */
    fun logOrDisableIfLimitsReached(input: CreateQueryLogInput): QueryLog? {
        val storedQuery = storedQueryService.findById(input.storedQueryId) ?: return null

        if (storedQueryService.isQuarantined(storedQuery)) {
            return null
        }

        val queryLogEntity =
            QueryLogEntity(
                storedQueryId = input.storedQueryId,
                message = input.message,
                startTime = input.startTime,
                endTime = input.endTime,
                traceId = input.traceId,
                requestCorrelationId = input.requestCorrelationId,
                appName = input.appName,
                main = true,
            )

        val count =
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) c FROM query_logs WHERE stored_query_id = ? AND main IS TRUE",
                Int::class.java,
                input.storedQueryId,
            )

        if (count >= storedQuery.queryLimit) {
            storedQueryService.disableQueryById(storedQuery.id)
            return null
        }

        val instant = Instant.now()
        val ts = Timestamp.from(instant)

        // We need to use jdbcTemplate here because Spring Data JDBC requires @Id for saving.
        jdbcTemplate.update {
            val ps =
                it.prepareStatement(
                    "INSERT INTO query_logs (stored_query_id, message, start_time, end_time, app_name, trace_id, created_at, main) VALUES (?, ?, ?, ?, ?, ?, ?, TRUE)",
                )
            ps.setInt(1, input.storedQueryId)
            ps.setString(2, input.message)
            ps.setLong(3, input.startTime)
            ps.setLong(4, input.endTime)
            ps.setString(5, input.appName)
            ps.setString(6, queryLogEntity.traceId)
            ps.setTimestamp(7, ts)
            ps
        }

        val queryLog =
            toDomainObject(
                queryLogEntity.copy(
                    createdAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()),
                ),
            )
        publisher.publishEvent(QueryLogsCreatedEvent(this, listOf(queryLog)))
        return queryLog
    }

    /** Stores as non-tracing log. */
    fun putTrace(storedQuery: QueryReport): QueryLog? {
        val storedQueryId = storedQuery.queryId.split(":")[1].toInt()

        if (storedQueryService.isQuarantined(storedQueryService.findById(storedQueryId)!!.applicationId)) {
            return null
        }

        val queryLogEntity =
            QueryLogEntity(
                storedQueryId = storedQuery.queryId.split(":")[1].toInt(),
                message = storedQuery.message,
                startTime = storedQuery.startTime,
                endTime = storedQuery.endTime,
                appName = storedQuery.appName,
                traceId = storedQuery.traceId,
                requestCorrelationId = storedQuery.requestCorrelationId,
                main = false,
            )

        val instant = Instant.now()
        val ts = Timestamp.from(instant)

        // We need to use jdbcTemplate here because Spring Data JDBC requires @Id for saving.
        jdbcTemplate.update {
            val ps =
                it.prepareStatement(
                    "INSERT INTO query_logs (stored_query_id, message, start_time, end_time, app_name, trace_id, created_at, main) VALUES (?, ?, ?, ?, ?, ?, ?, FALSE)",
                )
            ps.setInt(1, queryLogEntity.storedQueryId)
            ps.setString(2, queryLogEntity.message)
            ps.setLong(3, queryLogEntity.startTime)
            ps.setLong(4, queryLogEntity.endTime)
            ps.setString(5, queryLogEntity.appName)
            ps.setString(6, queryLogEntity.traceId)
            ps.setTimestamp(7, ts)
            ps
        }

        val queryLog =
            toDomainObject(
                queryLogEntity.copy(
                    createdAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()),
                ),
            )
        publisher.publishEvent(QueryLogsCreatedEvent(this, listOf(queryLog)))
        return queryLog
    }

    private fun toDomainObject(entity: QueryLogEntity) =
        QueryLog(
            storedQueryId = entity.storedQueryId,
            message = entity.message,
            startTime = entity.startTime,
            endTime = entity.endTime,
            traceId = entity.traceId,
            appName = entity.appName,
            requestCorrelationId = entity.requestCorrelationId,
            createdAt =
                entity.createdAt!!
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
        )

    data class CreateQueryLogInput(
        val storedQueryId: Int,
        val message: String,
        val startTime: Long,
        val endTime: Long,
        val traceId: String?,
        val requestCorrelationId: String?,
        val appName: String,
    )

    data class QueryLogFilterInput(
        val storedQueryId: Int,
    )

    data class TraceFilterInput(
        val storedQueryId: Int,
        val traceId: String,
    )
}
