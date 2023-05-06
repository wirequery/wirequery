package com.transactions.transactions

import org.springframework.stereotype.Repository

@Repository
class TransactionRepository {
    private val transactions = mutableListOf<Transaction>()

    fun findAll(account: String): List<Transaction> {
        return transactions.filter { it.fromAccount == account || it.toAccount == account }
    }

    fun save(transaction: Transaction): Transaction {
        return transaction
            .copy(id = transactions.size + 1)
            .also { transactions += it }
    }
}
