// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.balancecalculator.balance

import org.springframework.web.bind.annotation.*

@CrossOrigin
@RequestMapping("/balances")
@RestController
class BalanceController(
    private val balanceService: BalanceService
) {

    @GetMapping
    fun calculateBalance(@RequestHeader accountId: String): Balance {
        return balanceService.calculateBalance(accountId)
    }

}
