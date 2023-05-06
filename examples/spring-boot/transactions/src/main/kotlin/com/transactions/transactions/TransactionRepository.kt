package com.transactions.transactions

import com.transactions.transactions.Transaction.TransactionType.CREDIT
import com.transactions.transactions.Transaction.TransactionType.DEBIT
import org.springframework.stereotype.Repository

@Repository
class TransactionRepository {
    private val transactions = listOf(
        Transaction(
            id = 1,
            amount = 100,
            actualAmount = 112,
            currency = "USD",
            type = DEBIT,
            fromAccount = "NL69FAKE8085990849",
            toAccount = "13719713158835300",
            description = "some transaction"
        ),
        Transaction(
            id = 2,
            amount = 4500,
            actualAmount = 4500,
            currency = "EUR",
            type = CREDIT,
            fromAccount = "NL69FAKE8085990849",
            toAccount = "13719713158835300",
            description = "some transaction 2"
        ),
        Transaction(
            id = 3,
            amount = 100,
            actualAmount = 112,
            currency = "USD",
            type = CREDIT,
            fromAccount = "NL69FAKE8085990849",
            toAccount = "13719713158835300",
            description = "some transaction 3"
        )
    )

    fun findAll(account: String): List<Transaction> {
        return transactions.filter { it.fromAccount == account || it.toAccount == account }
    }

}
