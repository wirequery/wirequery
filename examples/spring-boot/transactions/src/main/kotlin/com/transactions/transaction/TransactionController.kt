package com.transactions.transaction

import org.springframework.web.bind.annotation.*

@RequestMapping("/transactions")
@RestController
class TransactionController(
    private val transactionRepository: TransactionRepository
) {

    @GetMapping
    fun findByAccount(@RequestHeader accountId: String) =
        transactionRepository.findAll(accountId)

}
