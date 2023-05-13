package com.wirequery.springboot2.starter

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "wirequery")
class WireQueryConfigurationProperties {
    var queries = listOf<QueryProperty>()
    var maskSettings = MaskSettings()
    var connection: Connection? = null

    class QueryProperty {
        var id = ""
        var query = ""
    }

    class Connection {
        var host = ""
        var appName = ""
        var apiKey = ""
    }

    class MaskSettings {
        var unmaskByDefault = false
        var requestHeaders = listOf<String>()
        var responseHeaders = listOf<String>()
    }
}
