// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

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
