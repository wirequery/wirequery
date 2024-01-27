// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.masking

interface HeadersMasker {
    fun maskRequestHeaders(value: Map<String, List<String>>): Map<String, List<String>>

    fun maskResponseHeaders(value: Map<String, List<String>>): Map<String, List<String>>
}
