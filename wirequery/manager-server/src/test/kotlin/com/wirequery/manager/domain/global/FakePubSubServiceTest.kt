// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.global

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import reactor.core.publisher.FluxSink

class FakePubSubServiceTest {
    @Test
    fun `simple publish subscribe`() {
        val service = FakePubSubService()
        val fluxSink = mock<FluxSink<String>>()
        service.subscribe("abc", fluxSink, String::class.java)
        service.publish("abc", "123")
        verify(fluxSink).next("123")
    }

    @Test
    fun `subscribe multiple`() {
        val service = FakePubSubService()
        val fluxSink1 = mock<FluxSink<String>>()
        val fluxSink2 = mock<FluxSink<String>>()
        service.subscribe("abc", fluxSink1, String::class.java)
        service.subscribe("abc", fluxSink2, String::class.java)
        service.publish("abc", "123")
        verify(fluxSink1).next("123")
        verify(fluxSink2).next("123")
    }

    @Test
    fun `subscription is removed after unsubscribe`() {
        val service = FakePubSubService()
        val fluxSink = mock<FluxSink<String>>()
        service.subscribe("abc", fluxSink, String::class.java)
        service.unsubscribe("abc", fluxSink)
        service.publish("abc", "123")
        verify(fluxSink, times(0)).next("123")
    }
}
