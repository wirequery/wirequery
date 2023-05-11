package com.wirequery.spring6

import com.fasterxml.jackson.databind.ObjectMapper
import com.wirequery.core.QueryLoader
import com.wirequery.core.ResultPublisher
import com.wirequery.core.TraceableQuery
import com.wirequery.core.query.QueryCompiler
import com.wirequery.core.query.context.AppHead
import com.wirequery.core.query.context.Query
import io.grpc.stub.StreamObserver
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import wirequerypb.Wirequery
import wirequerypb.WirequeryServiceGrpc
import javax.annotation.PostConstruct

class WireQueryAdapter(
    private val wireQueryStub: WirequeryServiceGrpc.WirequeryServiceStub,
    private val bridgeSettings: BridgeSettings,
    private val objectMapper: ObjectMapper,
    private val queryCompiler: QueryCompiler
) : QueryLoader, ResultPublisher {

    private var waitForNextMessageSeconds = 0

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
    fun listenForQueries() {
        synchronized(lock) {
            queries = listOf()
        }
        wireQueryStub.listenForQueries(Wirequery.ListenForQueriesRequest.newBuilder()
            .setAppName(bridgeSettings.appName)
            .setApiKey(bridgeSettings.apiKey)
            .build(), object : StreamObserver<Wirequery.QueryMutation> {
            override fun onNext(q: Wirequery.QueryMutation) {
                synchronized(lock) {
                    when {
                        q.hasAddQuery() ->
                            try {
                                queries = queries + createTraceableQuery(q)
                            } catch (e: Exception) {
                                messageQueue += Wirequery.QueryReport.newBuilder()
                                    .setMessage(objectMapper.writeValueAsString(mapOf("error" to e.message)))
                                    .setQueryId(q.addQuery.queryId)
                                    .build()
                            }
                        q.hasRemoveQueryById() ->
                            queries = queries.filterNot { it.name == q.removeQueryById }
                        else ->
                            error("Unknown query mutation")
                    }
                }
            }

            override fun onError(t: Throwable) {
                t.printStackTrace()
            }

            override fun onCompleted() {
                Thread.sleep(5000)
                listenForQueries()
            }
        })
    }

    // TODO different terminology used between Proto and us
    private fun createTraceableQuery(q: Wirequery.QueryMutation) = TraceableQuery(
        name = q.addQuery.queryId,
        compiledQuery = queryCompiler.compile(Query(
            appHead = AppHead(
                method = q.addQuery.method,
                path = q.addQuery.path,
                statusCode = q.addQuery.statusCode
            ),
            streamOperations = q.addQuery.expressionsList.map {
                Query.Operation(
                    name = it.function,
                    celExpression = it.celExpression?.let { if (it.isBlank()) null else it }
                )
            },
            aggregatorOperation = q.addQuery.aggregatorExpression?.let {
                if (it.function.isNotBlank()) {
                    Query.Operation(
                        name = it.function,
                        celExpression = it.celExpression?.let { if (it.isBlank()) null else it }
                    )
                } else null
            }
        ))
    )

    override fun getQueries(): List<TraceableQuery> {
        return queries
    }

    /** Queue the message onto a message queue so we bundle them. */
    override fun publishResult(query: TraceableQuery, results: Any) {
        messageQueue += Wirequery.QueryReport.newBuilder()
            .setMessage(objectMapper.writeValueAsString(mapOf("result" to results)))
            .setQueryId(query.name)
            .build()
    }

    @Scheduled(fixedRate = 200)
    fun schedulePublishing() {
        if (waitForNextMessageSeconds > 0) {
            waitForNextMessageSeconds--
            return
        }

        val toPublish = mutableListOf<Wirequery.QueryReport>()
        synchronized(lock) {
            toPublish.addAll(messageQueue)
            messageQueue.clear()
        }
        if (toPublish.isNotEmpty()) {
            waitForNextMessageSeconds = 3 // We will now send a message, so wait a while until we send the next one.
            wireQueryStub.reportQueryResults(
                Wirequery.QueryReports.newBuilder()
                    .setApiKey(bridgeSettings.apiKey)
                    .setAppName(bridgeSettings.appName)
                    .addAllQueryReports(toPublish)
                    .build(), object : StreamObserver<Wirequery.Empty> {
                    override fun onNext(value: Wirequery.Empty) {
                    }

                    override fun onError(t: Throwable) {
                        t.printStackTrace()
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
