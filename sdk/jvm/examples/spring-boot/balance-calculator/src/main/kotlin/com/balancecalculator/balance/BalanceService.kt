package com.balancecalculator.balance

import com.balancecalculator.clients.TransactionsClient
import com.balancecalculator.clients.TransactionsClient.Transaction
import com.balancecalculator.clients.TransactionsClient.Transaction.TransactionType.DEBIT
import org.springframework.stereotype.Service

@Service
class BalanceService(
    private val transactionsClient: TransactionsClient,
) {

    fun calculateBalance(accountId: String): Balance {
        val balance = transactionsClient.getTransactions(accountId)
        return Balance(
            balance = balance.sumOf { calculateAmount(it) },
            totalPerCurrency = balance
                .groupBy { it.currency }
                .map { it.key to calculateActualAmount(it.value) }
                .toMap()
        )
    }

    private fun calculateAmount(it: Transaction) =
        if (it.type == DEBIT)
            -it.amount
        else
            it.amount

    private fun calculateActualAmount(transactions: List<Transaction>): Int =
        transactions.sumOf { transaction ->
            if (transaction.type == DEBIT)
                -transaction.actualAmount
            else
                transaction.actualAmount
        }

}
