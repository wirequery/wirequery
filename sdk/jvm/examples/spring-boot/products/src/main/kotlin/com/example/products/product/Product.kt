// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.example.products.product

import com.wirequery.core.annotations.Unmask

@Unmask
data class Product(
    val id: String,
    val name: String,
    val price: Int
)
