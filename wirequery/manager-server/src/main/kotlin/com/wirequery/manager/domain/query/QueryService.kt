// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.query

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.FunctionalException.Companion.functionalError
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.global.PubSubService
import com.wirequery.manager.domain.query.QueryEvent.QueryEnteredEvent
import com.wirequery.manager.domain.query.QueryService.QueryMutation.*
import com.wirequery.manager.domain.tenant.TenantService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.time.Clock

@Service
@Transactional
class QueryService(
    private val queryIdGenerator: QueryIdGenerator,
    private val queryParserService: QueryParserService,
    private val clock: Clock,
    private val pubSubService: PubSubService,
    private val aggregatorService: AggregatorService,
    private val applicationService: ApplicationService,
    private val publisher: ApplicationEventPublisher,
    private val tenantService: TenantService,
) {
    fun startQuerying(
        queryId: String,
        expression: String,
    ) {
        val query = queryParserService.parse(expression)
        if (applicationService.isQuarantined(query.queryHead.appName)) {
            functionalError("Application is in quarantine: ${query.queryHead.appName}")
        }
        pubSubService.publish("query_additions:${query.queryHead.appName}", toAddQueryMutation(queryId, query))
    }

    fun stopQueryingByExpression(
        queryId: String,
        expression: String,
    ) {
        val query = queryParserService.parse(expression)
        pubSubService.publish("query_removals:${query.queryHead.appName}", RemoveQueryMutationById(queryId))
    }

    fun query(
        expression: String,
        sink: FluxSink<QueryReport>,
    ) {
        publisher.publishEvent(QueryEnteredEvent(this, expression))
        val queryId = queryIdGenerator.generateId()
        try {
            val query = queryParserService.parse(expression)
            if (applicationService.isQuarantined(query.queryHead.appName)) {
                functionalError("Application is in quarantine: ${query.queryHead.appName}")
            }
            val aggregator = aggregatorService.create(query)
            val flux =
                Flux
                    .create {
                        pubSubService.subscribe("query_report:$queryId", it, QueryReport::class.java)
                        pubSubService.publish(
                            "query_additions:${query.queryHead.appName}",
                            toAddQueryMutation(queryId, query),
                        )
                    }
                    .mapNotNull<QueryReport> { aggregator.apply(it) }
                    .subscribe { sink.next(it) }

            val tenantId = tenantService.tenantId
            sink.onDispose {
                pubSubService.publish(
                    "query_removals:${query.queryHead.appName}",
                    RemoveQueryMutationById(queryId),
                    tenantId,
                )
                flux.dispose()
            }
        } catch (e: FunctionalException) {
            val time = clock.millis()
            sink.next(
                QueryReport(
                    appName = "",
                    queryId = queryId,
                    message = OBJECT_MAPPER.writeValueAsString(mapOf("error" to e.message)),
                    startTime = time,
                    endTime = time,
                    traceId = null,
                    requestCorrelationId = null,
                ),
            )
            sink.complete()
        }
    }

    private fun toAddQueryMutation(
        queryId: String,
        query: QueryParserService.Query,
    ) = AddQueryMutation(
        queryId = queryId,
        queryHead = query.queryHead,
        operations = query.streamOperations,
        aggregatorOperation = query.aggregatorOperation,
    )

    fun subscribeToQueries(
        appName: String,
        sink: FluxSink<QueryMutation>,
    ) {
        val addQueryMutation =
            Flux.create {
                pubSubService.subscribe("query_additions:$appName", it, AddQueryMutation::class.java)
            }.subscribe {
                sink.next(it)
            }

        val removeQueryMutation =
            Flux.create {
                pubSubService.subscribe("query_removals:$appName", it, RemoveQueryMutationById::class.java)
            }.subscribe { sink.next(it) }

        val traceRequests =
            Flux.create {
                pubSubService.subscribe("trace_requests", it, QueryOneTrace::class.java)
            }.subscribe {
                sink.next(it)
            }

        sink.onDispose {
            addQueryMutation.dispose()
            removeQueryMutation.dispose()
            traceRequests.dispose()
            pubSubService.unsubscribe("query_additions:$appName", sink)
            pubSubService.unsubscribe("query_removals:$appName", sink)
            pubSubService.unsubscribe("trace_requests", sink)
        }
    }

    sealed interface QueryMutation {
        data class AddQueryMutation(
            val queryId: String,
            val queryHead: QueryParserService.QueryHead,
            val operations: List<QueryParserService.Operation>,
            val aggregatorOperation: QueryParserService.Operation?,
        ) : QueryMutation

        data class QueryOneTrace(
            val queryId: String,
            val traceId: String,
        ) : QueryMutation

        data class RemoveQueryMutationById(val id: String) : QueryMutation
    }

    private companion object {
        val OBJECT_MAPPER = jacksonObjectMapper()
    }
}
