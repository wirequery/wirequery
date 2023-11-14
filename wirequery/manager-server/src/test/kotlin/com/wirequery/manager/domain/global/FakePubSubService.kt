// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.global

import reactor.core.publisher.FluxSink

class FakePubSubService : PubSubService {
    private val pubSub = mutableMapOf<String, MutableList<FluxSink<Any>>>()

    override fun <T : Any> publish(
        channel: String,
        t: T,
        tenantId: Int?,
    ) {
        if (pubSub[channel] == null) {
            return
        }
        pubSub[channel]?.forEach {
            it.next(t as Any)
        }
    }

    override fun <T : Any> subscribe(
        channel: String,
        sink: FluxSink<T>,
        clazz: Class<T>,
    ) {
        if (pubSub[channel] == null) {
            pubSub[channel] = mutableListOf()
        }
        pubSub[channel]?.add(sink as FluxSink<Any>)
    }

    override fun <T : Any> unsubscribe(
        channel: String,
        sink: FluxSink<T>,
    ) {
        pubSub[channel]?.remove(sink as FluxSink<Any>)
    }
}
