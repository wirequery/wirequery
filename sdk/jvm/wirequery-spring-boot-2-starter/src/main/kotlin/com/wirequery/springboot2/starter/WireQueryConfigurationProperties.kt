package com.wirequery.springboot2.starter

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "wirequery")
class WireQueryConfigurationProperties {
    var maskSettings = MaskSettings()
    var connection: Connection? = null
    var allowedResources: List<ResourceAuthorizationSetting>? = null
    var unallowedResources: List<ResourceAuthorizationSetting>? = null

    class Connection {
        var host = ""
        var appName = ""
        var apiKey = ""
        var secure = true
    }

    class ResourceAuthorizationSetting {
        var path: String = ""
        var methods: List<String>? = null
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
