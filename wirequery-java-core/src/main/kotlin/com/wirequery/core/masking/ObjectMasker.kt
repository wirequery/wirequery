package com.wirequery.core.masking

import com.fasterxml.jackson.databind.JsonNode

interface ObjectMasker {
    /**
     * Masks the provided object and returns it as a JsonNode
     */
    fun mask(value: Any): JsonNode
}
