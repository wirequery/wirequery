// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.grpc

import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.query.QueryReport
import com.wirequery.manager.domain.query.QueryReportService
import com.wirequery.manager.domain.query.QueryService
import com.wirequery.manager.domain.storedquery.StoredQuery
import com.wirequery.manager.domain.storedquery.StoredQueryService
import com.wirequery.manager.domain.tenant.TenantRequestContext
import io.grpc.Status.UNAUTHENTICATED
import io.grpc.StatusException
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.context.request.AbstractRequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.FluxSink.OverflowStrategy.BUFFER
import wirequerypb.Wirequery
import wirequerypb.WirequeryServiceGrpc.WirequeryServiceImplBase

@GrpcService
class WirequeryServiceGrpcImpl(
    private val storedQueryService: StoredQueryService,
    private val queryService: QueryService,
    private val queryReportService: QueryReportService,
    private val queryParserService: QueryParserService,
    private val applicationService: ApplicationService,
    private val tenantRequestContext: TenantRequestContext,
) : WirequeryServiceImplBase() {
    override fun listenForQueries(
        request: Wirequery.ListenForQueriesRequest,
        responseObserver: StreamObserver<Wirequery.QueryMutation>,
    ) {
        withTenantContext(request.apiKey) {
            lateinit var sink: FluxSink<QueryService.QueryMutation>
            if (!applicationService.isApiKeyValid(request.appName ?: "", request.apiKey ?: "")) {
                responseObserver.onError(StatusException(UNAUTHENTICATED))
                return@withTenantContext
            }
            val storedQueries = storedQueryService.findEnabledByApplicationName(request.appName)
            storedQueries.forEach {
                responseObserver.onNext(toProtoQueryMutation(it))
            }
            Flux.create({
                queryService.subscribeToQueries(request.appName, it)
                sink = it
            }, BUFFER)
                .subscribe {
                    try {
                        logger.info("Subscribed for listenForQueries of " + request.appName)
                        when (it) {
                            is QueryService.QueryMutation.AddQueryMutation ->
                                logger.info("Add query mutation to " + it.queryHead.appName + ": " + it.queryId)

                            is QueryService.QueryMutation.QueryOneTrace ->
                                logger.info("Query one trace " + it.queryId + ": " + it.traceId)

                            is QueryService.QueryMutation.RemoveQueryMutationById ->
                                logger.info("Remove query by id: " + it.id)
                        }
                        responseObserver.onNext(toProtoQueryMutation(it))
                    } catch (e: Exception) {
                        sink.complete()
                        responseObserver.onCompleted()
                        if (e !is io.grpc.StatusRuntimeException) {
                            logger.info("An exception occurred while sending to " + request.appName + ". Closing...", e)
                            e.printStackTrace()
                        } else {
                            logger.info(
                                "An exception occurred while sending to " + request.appName + ". Status: ${e.status.code}. Closing...",
                                e,
                            )
                        }
                    }
                }
        }
    }

    override fun reportQueryResults(
        queryReports: Wirequery.QueryReports,
        responseObserver: StreamObserver<Wirequery.Empty>,
    ) {
        try {
            withTenantContext(queryReports.apiKey) {
                if (!applicationService.isApiKeyValid(queryReports.appName ?: "", queryReports.apiKey ?: "")) {
                    responseObserver.onError(StatusException(UNAUTHENTICATED))
                    return@withTenantContext
                }
                queryReportService.reportQueryResults(toDomainQueryReports(queryReports))
                responseObserver.onNext(Wirequery.Empty.newBuilder().build())
            }
        } finally {
            responseObserver.onCompleted()
        }
    }

    private fun <T> withTenantContext(
        apiKey: String,
        function: () -> T,
    ): T {
        return try {
            // Enable @RequestScope.
            RequestContextHolder.setRequestAttributes(InMemoryRequestAttributes())
            val tenantId = apiKey.split("/")[0].toIntOrNull() ?: error("Invalid API Key")
            tenantRequestContext.tenantId = tenantId
            function()
        } finally {
            RequestContextHolder.resetRequestAttributes()
        }
    }

    private fun toDomainQueryReports(queryReports: Wirequery.QueryReports): List<QueryReport> {
        return queryReports.queryReportsList.map {
            QueryReport(
                appName = queryReports.appName,
                queryId = it.queryId,
                message = it.message,
                startTime = it.startTime,
                endTime = it.endTime,
                traceId = it.traceId,
            )
        }
    }

    private fun toProtoQueryMutation(storedQuery: StoredQuery): Wirequery.QueryMutation {
        val parsedQuery = queryParserService.parse(storedQuery.query)
        return Wirequery.QueryMutation.newBuilder()
            .setAddQuery(
                Wirequery.Query.newBuilder().apply {
                    queryId = StoredQueryService.STORED_QUERY_PREFIX + storedQuery.id
                    queryHead =
                        Wirequery.QueryHead.newBuilder()
                            .setAppName(parsedQuery.queryHead.appName)
                            .setMethod(parsedQuery.queryHead.method)
                            .setPath(parsedQuery.queryHead.path)
                            .setStatusCode(parsedQuery.queryHead.statusCode)
                            .build()
                    parsedQuery.aggregatorOperation?.let {
                        aggregatorOperation =
                            Wirequery.Operation.newBuilder().apply {
                                name = it.name
                                it.celExpression?.let(::setCelExpression)
                            }.build()
                    }
                    addAllStreamOperations(
                        parsedQuery.streamOperations.map {
                            Wirequery.Operation.newBuilder().apply {
                                name = it.name
                                it.celExpression?.let(::setCelExpression)
                                build()
                            }.build()
                        },
                    )
                },
            ).build()
    }

    private fun toProtoQueryMutation(queryMutation: QueryService.QueryMutation): Wirequery.QueryMutation {
        return when (queryMutation) {
            is QueryService.QueryMutation.AddQueryMutation ->
                Wirequery.QueryMutation.newBuilder()
                    .setAddQuery(
                        Wirequery.Query.newBuilder().apply {
                            queryId = queryMutation.queryId
                            queryHead =
                                Wirequery.QueryHead.newBuilder()
                                    .setAppName(queryMutation.queryHead.appName)
                                    .setMethod(queryMutation.queryHead.method)
                                    .setPath(queryMutation.queryHead.path)
                                    .setStatusCode(queryMutation.queryHead.statusCode)
                                    .build()
                            queryMutation.aggregatorOperation?.let {
                                aggregatorOperation =
                                    Wirequery.Operation.newBuilder().apply {
                                        name = it.name
                                        it.celExpression?.let(::setCelExpression)
                                    }.build()
                            }
                            addAllStreamOperations(
                                queryMutation.operations.map {
                                    Wirequery.Operation.newBuilder().apply {
                                        name = it.name
                                        it.celExpression?.let(::setCelExpression)
                                        build()
                                    }.build()
                                },
                            )
                        },
                    )
                    .build()

            is QueryService.QueryMutation.QueryOneTrace ->
                Wirequery.QueryMutation.newBuilder()
                    .setQueryOneTrace(
                        Wirequery.QueryOneTrace.newBuilder()
                            .setQueryId(queryMutation.queryId)
                            .setTraceId(queryMutation.traceId)
                            .build(),
                    )
                    .build()

            is QueryService.QueryMutation.RemoveQueryMutationById ->
                Wirequery.QueryMutation.newBuilder()
                    .setRemoveQueryById(queryMutation.id)
                    .build()
        }
    }

    class InMemoryRequestAttributes : AbstractRequestAttributes() {
        private val attributes: MutableMap<String, Any> = HashMap()

        override fun getAttribute(
            name: String,
            scope: Int,
        ): Any? {
            return attributes[name]
        }

        override fun setAttribute(
            name: String,
            value: Any,
            scope: Int,
        ) {
            attributes[name] = value
        }

        override fun removeAttribute(
            name: String,
            scope: Int,
        ) {
            attributes.remove(name)
        }

        override fun getAttributeNames(scope: Int): Array<String> {
            return attributes.keys.toTypedArray()
        }

        override fun registerDestructionCallback(
            name: String,
            callback: Runnable,
            scope: Int,
        ) {
            synchronized(requestDestructionCallbacks) { requestDestructionCallbacks.put(name, callback) }
        }

        override fun resolveReference(key: String): Any {
            return attributes
        }

        override fun getSessionId(): String {
            return ""
        }

        override fun getSessionMutex(): Any {
            return ""
        }

        override fun updateAccessedSessionAttributes() {}
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(com.wirequery.manager.application.db.DatasourceConfig::class.java)
    }
}
