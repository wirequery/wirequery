// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.transactions.transaction

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RequestMapping("/transactions")
@RestController
class TransactionController(
    private val transactionRepository: TransactionRepository
) {

    @GetMapping
    fun findByAccount(@RequestHeader accountId: String): ResponseEntity<List<Transaction>> =
        ResponseEntity.ok(transactionRepository.findAll(accountId))

}
