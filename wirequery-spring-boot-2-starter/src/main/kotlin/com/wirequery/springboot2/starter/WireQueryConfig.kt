package com.wirequery.springboot2.starter

import com.fasterxml.jackson.databind.ObjectMapper
import com.wirequery.core.QueryLoader
import com.wirequery.core.ResultPublisher
import com.wirequery.core.TraceableQuery
import com.wirequery.core.masking.ObjectMasker
import com.wirequery.core.masking.impl.ClassAnalyzingMaskDeterminer
import com.wirequery.core.masking.impl.SettingsBasedHeaderMaskDeterminer
import com.wirequery.core.masking.impl.SimpleHeadersMasker
import com.wirequery.core.masking.impl.SimpleObjectMasker
import com.wirequery.core.query.*
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class WireQueryConfig {

    private val logger = LoggerFactory.getLogger(this::class.java)

    // We don't provide the ObjectMapper as a bean here, as it may conflict
    // with the consumer's ObjectMapper usage, and because the beans that use the ObjectMapper
    // are trivial and can easily be replaced.
    private val defaultObjectMapper = ObjectMapper()

    @Bean
    @ConditionalOnMissingBean
    open fun queryParser() = QueryParser()

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
    open fun queryLoader(
        queryParser: QueryParser,
        queryCompiler: QueryCompiler,
        config: WireQueryConfigurationProperties
    ) = object : QueryLoader {
        override fun getQueries(): List<TraceableQuery> {
            return config.queries.map {
                TraceableQuery(
                    name = it.name,
                    compiledQuery = queryCompiler.compile(queryParser.parse(it.query))
                )
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    open fun resultPublisher() = object : ResultPublisher {
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
}
