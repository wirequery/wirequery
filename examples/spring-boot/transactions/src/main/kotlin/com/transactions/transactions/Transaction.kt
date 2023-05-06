package com.transactions.transactions

data class Transaction(
    val id: Int? = null,
    val amount: Int,
    val fromAccount: String,
    val toAccount: String,
    val description: String
)
