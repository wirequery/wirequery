package com.wirequery.spring6

import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestData {
    internal var requestBody: Any? = null
    internal var responseBody: Any? = null
    val extensions: Map<String, Any> = mutableMapOf()

    fun putExtension(key: String, value: Any) {
        if (extensions.containsKey(key)) {
            error("$key is already set")
        }
        (extensions as MutableMap<String, Any>)[key] = value
    }

}
