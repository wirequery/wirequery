// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

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
