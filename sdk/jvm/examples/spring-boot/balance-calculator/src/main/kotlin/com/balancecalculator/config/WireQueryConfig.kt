package com.balancecalculator.config

import com.wirequery.spring6.TraceProvider
import io.micrometer.tracing.Tracer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WireQueryConfig {

    @Bean
    fun traceProvider(tracer: Tracer) = TraceProvider {
        tracer.currentSpan()?.context()?.traceId()
    }

}
