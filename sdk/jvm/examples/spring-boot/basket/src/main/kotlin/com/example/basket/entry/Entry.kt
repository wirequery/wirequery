// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.example.basket.entry

import com.wirequery.core.annotations.Unmask

@Unmask
data class Entry(
    val id: String,
    val name: String,
    val amount: Int,
    val unitPrice: Int,
    val totalPrice: Int,
)
