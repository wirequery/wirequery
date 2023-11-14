// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.transactions.config

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
