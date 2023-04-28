package com.wirequery.core.masking.impl

import com.wirequery.core.masking.MaskingConstants.MASKING_LABEL

class SimpleHeadersMasker(
    private val headerMaskDeterminer: com.wirequery.core.masking.HeaderMaskDeterminer
) : com.wirequery.core.masking.HeadersMasker {
    override fun maskRequestHeaders(value: Map<String, List<String>>): Map<String, List<String>> {
        return value.entries.associate { e ->
            e.key to e.value.map {
                if (headerMaskDeterminer.shouldUnmaskRequestHeader(e.key)) it else MASKING_LABEL
            }
        }
    }

    override fun maskResponseHeaders(value: Map<String, List<String>>): Map<String, List<String>> {
        return value.entries.associate { e ->
            e.key to e.value.map {
                if (headerMaskDeterminer.shouldUnmaskResponseHeader(e.key)) it else MASKING_LABEL
            }
        }
    }
}
