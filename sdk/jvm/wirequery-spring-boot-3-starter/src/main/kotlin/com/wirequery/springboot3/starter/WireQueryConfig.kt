package com.wirequery.springboot3.starter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.wirequery.core.QueryLoader
import com.wirequery.core.ResultPublisher
import com.wirequery.core.TraceableQuery
import com.wirequery.core.masking.ObjectMasker
import com.wirequery.core.masking.impl.ClassAnalyzingMaskDeterminer
import com.wirequery.core.masking.impl.SettingsBasedHeaderMaskDeterminer
import com.wirequery.core.masking.impl.SimpleHeadersMasker
import com.wirequery.core.masking.impl.SimpleObjectMasker
import com.wirequery.core.query.*
import com.wirequery.spring6.WireQueryAdapter
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import wirequerypb.WirequeryServiceGrpc

@Configuration
@EnableAsync
@EnableScheduling
open class WireQueryConfig {

    private val logger = LoggerFactory.getLogger(this::class.java)

    // We don't provide the ObjectMapper as a bean here, as it may conflict
    // with the consumer's ObjectMapper usage, and because the beans that use the ObjectMapper
    // are trivial and can easily be replaced.
    private val defaultObjectMapper = ObjectMapper().registerModule(JavaTimeModule())

    @Bean
    @ConditionalOnMissingBean
    open fun queryParser(): QueryParser {
        return QueryParser()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun queryCompiler() = QueryCompiler(ExpressionCompiler())

    @Bean
    @ConditionalOnMissingBean
    open fun queryEvaluator(objectMasker: ObjectMasker, config: WireQueryConfigurationProperties) = QueryEvaluator(
        AppHeadEvaluator(),
        StreamOperationEvaluator(),
        AggregatorOperationEvaluator(),
        ContextMapCreator(
            SimpleHeadersMasker(
                SettingsBasedHeaderMaskDeterminer(
                    SettingsBasedHeaderMaskDeterminer.MaskSettings(
                        unmaskByDefault = config.maskSettings.unmaskByDefault,
                        requestHeaders = config.maskSettings.requestHeaders.toSet(),
                        responseHeaders = config.maskSettings.responseHeaders.toSet(),
                    )
                )
            ),
            objectMasker
        )
    )

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "wirequery.connection",  havingValue = "true", name = ["local"])
    open fun staticQueryLoader(
        queryParser: QueryParser,
        queryCompiler: QueryCompiler,
        config: WireQueryConfigurationProperties
    ) = object : QueryLoader {
        val cache = mutableMapOf<String, TraceableQuery>()

        override fun getQueries(): List<TraceableQuery> {
            return config.queries.map {
                cache.getOrPut(it.name) {
                    TraceableQuery(
                        name = it.name,
                        compiledQuery = queryCompiler.compile(queryParser.parse(it.query))
                    )
                }
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "wirequery.connection",  havingValue = "true", name = ["enable"])
    open fun staticResultPublisher() = object : ResultPublisher {
        override fun publishResult(query: TraceableQuery, results: Any) {
            val resultsStr = defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results)
            logger.info("WireQuery Report (${query.name}): " + resultsStr)
        }
    }

    @Bean
    @ConditionalOnMissingBean
    open fun simpleObjectMasker(
        config: WireQueryConfigurationProperties
    ): ObjectMasker = SimpleObjectMasker(
        defaultObjectMapper,
        ClassAnalyzingMaskDeterminer(unmaskByDefault = config.maskSettings.unmaskByDefault)
    )

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "wirequery.connection",  havingValue = "false", name = ["local"], matchIfMissing = true)
    open fun wireQueryAdapter(
        queryCompiler: QueryCompiler,
        config: WireQueryConfigurationProperties
    ): WireQueryAdapter = config.connection?.let { conn ->
        WireQueryAdapter(
            wireQueryStub = WirequeryServiceGrpc.newStub(ManagedChannelBuilder.forTarget(conn.url).usePlaintext().build()),
            bridgeSettings = WireQueryAdapter.BridgeSettings(
                appName = conn.appName,
                apiKey = conn.apiKey
            ),
            objectMapper = defaultObjectMapper,
            queryCompiler = queryCompiler,
        )
    } ?: error("No connection specified for WireQuery.")
}
