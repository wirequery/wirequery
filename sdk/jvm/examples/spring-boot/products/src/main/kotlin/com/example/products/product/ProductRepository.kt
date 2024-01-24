// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.example.products.product

import org.springframework.stereotype.Repository

@Repository
class ProductRepository {

    fun findAll(): List<Product> {
        return listOf(
            Product(
                id = "1",
                name = "Keyboard",
                price = 3000
            ),
            Product(
                id = "2",
                name = "4K Display",
                price = 30000
            ),
            Product(
                id = "3",
                name = "Mouse",
                price = 2000
            ),
        )
    }

}
