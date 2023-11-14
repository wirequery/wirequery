// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.masking

interface ClassFieldMaskDeterminer {
    /**
     * Determines whether the field on the provided value should be unmasked.
     */
    fun shouldUnmask(value: Any, fieldName: String): Boolean
}
