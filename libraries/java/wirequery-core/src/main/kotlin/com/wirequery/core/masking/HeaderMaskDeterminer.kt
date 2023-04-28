package com.wirequery.core.masking

interface HeaderMaskDeterminer {
    /**
     * Determines whether the request header should be unmasked
     */
    fun shouldUnmaskRequestHeader(name: String): Boolean

    /**
     * Determines whether the response header should be unmasked
     */
    fun shouldUnmaskResponseHeader(name: String): Boolean
}
