// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.masking.impl

import com.wirequery.core.masking.HeaderMaskDeterminer

class SettingsBasedHeaderMaskDeterminer(private val maskSettings: MaskSettings) : HeaderMaskDeterminer {
    override fun shouldUnmaskRequestHeader(name: String): Boolean {
        return (name in maskSettings.requestHeaders) xor maskSettings.unmaskByDefault
    }

    override fun shouldUnmaskResponseHeader(name: String): Boolean {
        return (name in maskSettings.responseHeaders) xor maskSettings.unmaskByDefault
    }

    data class MaskSettings(
        val unmaskByDefault: Boolean,
        val requestHeaders: Set<String>,
        val responseHeaders: Set<String>,
    )
}
