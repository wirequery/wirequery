package com.wirequery.manager.application.redis

import com.wirequery.manager.domain.global.PubSubService
import com.wirequery.manager.domain.tenant.TenantService
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.FluxSink

@Service
@Transactional
@Profile("!test")
class RedisPubSubService(
    private val redisContainer: RedisMessageListenerContainer,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val redisSerializer: GenericJackson2JsonRedisSerializer,
    private val tenantService: TenantService,
) : PubSubService {
    private val messageListeners: MutableMap<Pair<String, Any>, MessageListener> = mutableMapOf()

    override fun <T : Any> publish(
        channel: String,
        t: T,
        tenantId: Int?,
    ) {
        redisTemplate.convertAndSend("${tenantId ?: tenantService.tenantId}:pubsub:$channel", t)
    }

    override fun <T : Any> subscribe(
        channel: String,
        sink: FluxSink<T>,
        clazz: Class<T>,
    ) {
        val key = channel to sink
        val messageListener =
            MessageListener { message, _ ->
                redisSerializer.deserialize(message.body, clazz)?.let {
                    sink.next(it)
                }
            }
        messageListeners[key] = messageListener

        redisContainer.addMessageListener(messageListener, ChannelTopic.of("${tenantService.tenantId}:pubsub:$channel"))
    }

    override fun <T : Any> unsubscribe(
        channel: String,
        sink: FluxSink<T>,
    ) {
        messageListeners[channel to sink]?.run(redisContainer::removeMessageListener)
    }
}
