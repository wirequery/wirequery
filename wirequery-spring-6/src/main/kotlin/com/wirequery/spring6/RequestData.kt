package com.wirequery.spring6

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestData {
    internal var requestBody: Any? = null
    internal var responseBody: Any? = null
    internal val extensions: Map<String, JsonNode> = mutableMapOf()

    fun putExtension(key: String, value: JsonNode) {
        if (extensions.containsKey(key)) {
            error("$key is already set")
        }
        (extensions as MutableMap<String, JsonNode>)[key] = value
    }

}
