package com.wirequery.manager.application.graphql

import com.wirequery.manager.domain.global.FakePubSubService
import com.wirequery.manager.domain.global.PubSubService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TestConfig {
    @Bean
    @Primary
    fun pubSubService(): PubSubService {
        return FakePubSubService()
    }
}
