package com.wirequery.manager.domain.global

import reactor.core.publisher.FluxSink

interface PubSubService {
    fun <T : Any> publish(
        channel: String,
        t: T,
        tenantId: Int? = null,
    )

    /** Should unsubscribe when sink is disposed */
    fun <T : Any> subscribe(
        channel: String,
        sink: FluxSink<T>,
        clazz: Class<T>,
    )

    fun <T : Any> unsubscribe(
        channel: String,
        sink: FluxSink<T>,
    )
}
