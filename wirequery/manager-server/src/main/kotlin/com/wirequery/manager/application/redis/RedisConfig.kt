// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.redis

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

@Configuration
@Profile("!test")
class RedisConfig {
    @Bean
    fun connectionFactory(
        @Value("\${wirequery.redis.hostName}") hostName: String,
        @Value("\${wirequery.redis.port}") port: Int,
    ): LettuceConnectionFactory {
        val factory = LettuceConnectionFactory()
        factory.standaloneConfiguration.hostName = hostName
        factory.standaloneConfiguration.port = port
        return factory
    }

    @Bean
    fun redisContainer(connectionFactory: LettuceConnectionFactory): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().also {
            it.setConnectionFactory(connectionFactory)
        }
    }

    @Bean
    fun redisSerializer() = GenericJackson2JsonRedisSerializer(jacksonObjectMapper().registerKotlinModule())

    @Bean
    fun redisTemplate(
        lettuceConnectionFactory: LettuceConnectionFactory,
        redisSerializer: GenericJackson2JsonRedisSerializer,
    ): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = lettuceConnectionFactory
        template.valueSerializer = redisSerializer
        return template
    }
}
