package com.wirequery.core.masking

interface ObjectMasker {
    /**
     * Masks the provided object and returns it as a JsonNode
     */
    fun mask(value: Any): Any?
}
