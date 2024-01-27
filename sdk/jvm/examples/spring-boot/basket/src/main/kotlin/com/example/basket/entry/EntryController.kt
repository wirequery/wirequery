// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.example.basket.entry

import org.springframework.web.bind.annotation.*

@CrossOrigin
@RequestMapping("/basket-entries")
@RestController
class EntryController(
    private val entryService: EntryService,
) {
    @GetMapping
    fun getEntries(
        @RequestHeader accountId: String,
    ): List<Entry> {
        return entryService.getEntries(accountId)
    }

    @PostMapping("/{productId}")
    fun addToBasket(
        @RequestHeader accountId: String,
        @PathVariable productId: String,
        @RequestBody entry: EntryInput,
    ) {
        return entryService.addToBasket(accountId, productId, entry.quantity)
    }

    @DeleteMapping("/{productId}")
    fun removeFromBasket(
        @RequestHeader accountId: String,
        @PathVariable productId: String,
    ) {
        return entryService.removeFromBasket(accountId, productId)
    }

    data class EntryInput(
        val quantity: Int,
    )
}
