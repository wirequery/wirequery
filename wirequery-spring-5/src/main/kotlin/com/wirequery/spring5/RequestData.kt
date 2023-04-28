package com.wirequery.spring5

import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestData(
    internal var requestBody: Any? = null,
    internal var responseBody: Any? = null,
    val extensions: Map<String, Any> = mutableMapOf()
)
