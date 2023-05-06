package com.transactions.transactions

import org.springframework.web.bind.annotation.*

@RequestMapping("/transactions")
@RestController
class TransactionController(
    private val transactionRepository: TransactionRepository
) {

    @GetMapping("/{account}")
    fun findByAccount(@PathVariable account: String) =
        transactionRepository.findAll(account)

}
