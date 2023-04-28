package com.wirequery.springboot3.starter

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "wirequery")
class WireQueryConfigurationProperties {
    var queries = listOf<QueryProperty>()
    var maskSettings = MaskSettings()

    class QueryProperty {
        var name = ""
        var query = ""
    }

    class MaskSettings {
        var unmaskByDefault = false
        var requestHeaders = listOf<String>()
        var responseHeaders = listOf<String>()
    }
}
