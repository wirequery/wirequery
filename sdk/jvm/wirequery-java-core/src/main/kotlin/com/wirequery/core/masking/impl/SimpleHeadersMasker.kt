// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.masking.impl

import com.wirequery.core.masking.HeadersMasker
import com.wirequery.core.masking.MaskingConstants.MASKING_LABEL

class SimpleHeadersMasker(
    private val headerMaskDeterminer: com.wirequery.core.masking.HeaderMaskDeterminer,
) : HeadersMasker {
    override fun maskRequestHeaders(value: Map<String, List<String>>): Map<String, List<String>> {
        return value.entries.associate { e ->
            e.key to
                e.value.map {
                    if (headerMaskDeterminer.shouldUnmaskRequestHeader(e.key)) it else MASKING_LABEL
                }
        }
    }

    override fun maskResponseHeaders(value: Map<String, List<String>>): Map<String, List<String>> {
        return value.entries.associate { e ->
            e.key to
                e.value.map {
                    if (headerMaskDeterminer.shouldUnmaskResponseHeader(e.key)) it else MASKING_LABEL
                }
        }
    }
}
