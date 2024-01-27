// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring6

import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestData {
    internal var requestBody: Any? = null
    internal var responseBody: Any? = null
    internal var startTime: Long = 0

    val extensions: Map<String, Any> = mutableMapOf()

    fun putExtension(
        key: String,
        value: Any,
    ) {
        if (extensions.containsKey(key)) {
            error("$key is already set")
        }
        (extensions as MutableMap<String, Any>)[key] = value
    }
}
