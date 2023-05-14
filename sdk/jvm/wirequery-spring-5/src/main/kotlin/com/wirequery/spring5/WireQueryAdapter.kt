package com.wirequery.spring5

import com.fasterxml.jackson.databind.ObjectMapper
import com.wirequery.core.QueryLoader
import com.wirequery.core.ResultPublisher
import com.wirequery.core.TraceableQuery
import com.wirequery.core.query.QueryCompiler
import com.wirequery.core.query.context.QueryHead
import com.wirequery.core.query.context.Query
import io.grpc.stub.StreamObserver
import org.springframework.scheduling.annotation.Scheduled
import wirequerypb.Wirequery
import wirequerypb.WirequeryServiceGrpc
import javax.annotation.PostConstruct

class WireQueryAdapter(
    private val wireQueryStub: WirequeryServiceGrpc.WirequeryServiceStub,
    private val bridgeSettings: BridgeSettings,
    private val objectMapper: ObjectMapper,
    private val queryCompiler: QueryCompiler,
    private val logger: Logger,
    private val sleeper: Sleeper
) : QueryLoader, ResultPublisher {

    private var waitForNextMessageCycles = 0

    val lock = Any()

    private var messageQueue = mutableListOf<Wirequery.QueryReport>()
    private var queries = listOf<TraceableQuery>()

    @PostConstruct
    fun init() {
        listenForQueries()
    }

    /**
     * Listen for query mutations. Whenever it is completed, try again after 5 seconds.
     */
    private fun listenForQueries() {
        synchronized(lock) {
            queries = listOf()
        }
        logger.debug("Listening for queries...")
        wireQueryStub.listenForQueries(
            Wirequery.ListenForQueriesRequest.newBuilder()
                .setAppName(bridgeSettings.appName)
                .setApiKey(bridgeSettings.apiKey)
                .build(),
            object : StreamObserver<Wirequery.QueryMutation> {
                override fun onNext(q: Wirequery.QueryMutation) {
                    synchronized(lock) {
                        when {
                            q.hasAddQuery() ->
                                try {
                                    queries = queries + createTraceableQuery(q)
                                } catch (e: Exception) {
                                    publishError(q.addQuery.queryId, "" + e.message)
                                }

                            q.hasRemoveQueryById() ->
                                queries = queries.filterNot { it.queryId == q.removeQueryById }

                            else ->
                                error("Unknown query mutation")
                        }
                    }
                }

                override fun onError(t: Throwable) {
                    logger.warn("An error occured while listening for queries: $t. Reconnecting in 5 seconds...")
                    sleeper.sleep(5000)
                    listenForQueries()
                }

                override fun onCompleted() {
                    logger.debug("The stream for listening to queries was completed. Reconnecting in 5 seconds...")
                    sleeper.sleep(5000)
                    listenForQueries()
                }
            })
    }

    private fun createTraceableQuery(q: Wirequery.QueryMutation) = TraceableQuery(
        queryId = q.addQuery.queryId,
        compiledQuery = queryCompiler.compile(Query(
            queryHead = QueryHead(
                method = q.addQuery.queryHead.method,
                path = q.addQuery.queryHead.path,
                statusCode = q.addQuery.queryHead.statusCode
            ),
            streamOperations = q.addQuery.streamOperationsList.map {
                Query.Operation(
                    name = it.name,
                    celExpression = it.celExpression.ifBlank { null }
                )
            },
            aggregatorOperation = q.addQuery.aggregatorOperation?.let {
                if (it.name.isBlank())
                    null
                else
                    Query.Operation(
                        name = it.name,
                        celExpression = it.celExpression.ifBlank { null }
                    )
            }
        ))
    )

    override fun getQueries(): List<TraceableQuery> {
        return queries
    }

    override fun publishResult(query: TraceableQuery, results: Any) {
        messageQueue += Wirequery.QueryReport.newBuilder()
            .setMessage(objectMapper.writeValueAsString(mapOf("result" to results)))
            .setQueryId(query.queryId)
            .build()
    }

    override fun publishError(queryId: String, message: String) {
        messageQueue += Wirequery.QueryReport.newBuilder()
            .setMessage(objectMapper.writeValueAsString(mapOf("error" to message)))
            .setQueryId(queryId)
            .build()
    }

    @Scheduled(fixedRate = 200)
    fun schedulePublishing() {
        if (waitForNextMessageCycles > 0) {
            waitForNextMessageCycles--
            return
        }

        val toPublish = mutableListOf<Wirequery.QueryReport>()
        synchronized(lock) {
            toPublish.addAll(messageQueue)
            messageQueue.clear()
        }
        if (toPublish.isNotEmpty()) {
            waitForNextMessageCycles = 3 // We will now send a message, so wait a while until we send the next one.
            logger.info("Publish ${toPublish.size} reports...")
            wireQueryStub.reportQueryResults(
                Wirequery.QueryReports.newBuilder()
                    .setApiKey(bridgeSettings.apiKey)
                    .setAppName(bridgeSettings.appName)
                    .addAllQueryReports(toPublish)
                    .build(),
                object : StreamObserver<Wirequery.Empty> {
                    override fun onNext(value: Wirequery.Empty) {
                    }

                    override fun onError(t: Throwable) {
                        logger.warn("An error occurred while reporting query results: $t. Dropping...")
                    }

                    override fun onCompleted() {
                    }
                })
        }
    }

    data class BridgeSettings(
        val appName: String,
        val apiKey: String,
    )
}
