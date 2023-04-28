package com.wirequery.core.masking.impl

class SettingsBasedHeaderMaskDeterminer(private val maskSettings: MaskSettings) :
    com.wirequery.core.masking.HeaderMaskDeterminer {
    override fun shouldUnmaskRequestHeader(name: String): Boolean {
        return (name in maskSettings.requestHeaders) xor maskSettings.unmaskByDefault
    }

    override fun shouldUnmaskResponseHeader(name: String): Boolean {
        return (name in maskSettings.responseHeaders) xor maskSettings.unmaskByDefault
    }

    data class MaskSettings(
        val unmaskByDefault: Boolean,
        val requestHeaders: Set<String>,
        val responseHeaders: Set<String>
    )
}
