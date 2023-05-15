package com.wirequery.springboot2.starter

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "wirequery")
class WireQueryConfigurationProperties {
    var queries = listOf<QueryProperty>()
    var maskSettings = MaskSettings()
    var connection: Connection? = null
    var allowedPaths: List<PathSetting>? = null
    var unallowedPaths: List<PathSetting>? = null

    class QueryProperty {
        var id = ""
        var query = ""
    }

    class Connection {
        var host = ""
        var appName = ""
        var apiKey = ""
        var secure = true
    }

    class PathSetting {
        var method: String? = null
        var path: String = ""
    }

    class MaskSettings {
        var unmaskByDefault = false
        var requestHeaders = listOf<String>()
        var responseHeaders = listOf<String>()
        var classes = listOf<AdditionalClass>()
    }

    class AdditionalClass {
        var mask: Boolean? = null
        var unmask: Boolean? = null
        var name: String = ""
        var fields = listOf<AdditionalField>()
    }

    class AdditionalField {
        var mask: Boolean? = null
        var unmask: Boolean? = null
        var name: String = ""
    }
}
