package com.transactions.transactions

import com.wirequery.core.annotations.Unmask

data class Transaction(
    val id: Int? = null,
    @Unmask
    val amount: Int,
    @Unmask
    val actualAmount: Int,
    @Unmask
    val currency: String,
    @Unmask
    val type: TransactionType,
    val fromAccount: String,
    val toAccount: String,
    val description: String
) {
    enum class TransactionType {
        DEBIT,
        CREDIT
    }
}
