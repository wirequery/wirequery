package com.wirequery.core.masking

interface ClassFieldMaskDeterminer {
    /**
     * Determines whether the field on the provided value should be unmasked.
     */
    fun shouldUnmask(value: Any, fieldName: String): Boolean
}
