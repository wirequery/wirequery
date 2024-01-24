// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.example.products.product

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RequestMapping("/products")
@RestController
class ProductController(
    private val productRepository: ProductRepository
) {

    @GetMapping
    fun findAll(): ResponseEntity<List<Product>> =
        ResponseEntity.ok(productRepository.findAll())

}
