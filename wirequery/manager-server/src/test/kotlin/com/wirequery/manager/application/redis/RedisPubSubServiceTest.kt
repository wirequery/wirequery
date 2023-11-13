package com.wirequery.manager.application.redis

import com.wirequery.manager.domain.tenant.TenantService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import reactor.core.publisher.FluxSink

@ExtendWith(MockitoExtension::class)
class RedisPubSubServiceTest {
    @Mock
    private lateinit var redisContainer: RedisMessageListenerContainer

    @Mock
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Mock
    private lateinit var redisSerializer: GenericJackson2JsonRedisSerializer

    @Mock
    private lateinit var tenantService: TenantService

    @InjectMocks
    private lateinit var redisPubSubService: RedisPubSubService

    @Test
    fun `publish passes the message to the corresponding pubsub channel in Redis`() {
        whenever(tenantService.tenantId).thenReturn(1)
        val someValue = "some-value"
        redisPubSubService.publish("some-channel", someValue)
        verify(redisTemplate).convertAndSend("1:pubsub:some-channel", someValue)
    }

    @Test
    fun `publish allows custom tenant id to be passed`() {
        val someValue = "some-value"
        redisPubSubService.publish("some-channel", someValue, 1)
        verify(redisTemplate).convertAndSend("1:pubsub:some-channel", someValue)
    }

    @Test
    fun `subscribe subscribes to messages from the corresponding pubsub channel in Redis`() {
        whenever(tenantService.tenantId).thenReturn(1)
        val captor = argumentCaptor<MessageListener>()
        val sink = mock<FluxSink<String>>()
        val someSerializedValue = "some-value".toByteArray()
        val someValue = "some-value"

        whenever(redisSerializer.deserialize(someSerializedValue, String::class.java))
            .thenReturn(someValue)

        redisPubSubService.subscribe("some-channel", sink, String::class.java)

        verify(redisContainer).addMessageListener(captor.capture(), eq(ChannelTopic.of("1:pubsub:some-channel")))

        val message = mock<Message>()
        whenever(message.body).thenReturn(someSerializedValue)
        captor.firstValue.onMessage(message, null)

        verify(sink).next(someValue)
    }

    @Test
    fun `subscribe unsubscribes to messages from the corresponding pubsub channel in Redis`() {
        whenever(tenantService.tenantId).thenReturn(1)
        val captor = argumentCaptor<MessageListener>()
        val sink = mock<FluxSink<String>>()

        redisPubSubService.subscribe("some-channel", sink, String::class.java)
        redisPubSubService.unsubscribe("some-channel", sink)

        verify(redisContainer).addMessageListener(captor.capture(), eq(ChannelTopic.of("1:pubsub:some-channel")))
        verify(redisContainer).removeMessageListener(eq(captor.firstValue))
    }
}
