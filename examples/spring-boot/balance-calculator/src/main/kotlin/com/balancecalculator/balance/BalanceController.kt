package com.balancecalculator.balance

import org.springframework.web.bind.annotation.*

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
