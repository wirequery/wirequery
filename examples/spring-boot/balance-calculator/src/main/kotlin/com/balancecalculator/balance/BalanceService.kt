package com.balancecalculator.balance

import com.balancecalculator.clients.TransactionsClient
import org.springframework.stereotype.Service

@Service
class BalanceService(
    private val transactionsClient: TransactionsClient,
) {

    fun calculateBalance(accountId: String): Balance {
        val balance = transactionsClient.getTransactions(accountId)
        return Balance(
            balance = balance.sumOf { it.amount },
            totalPerCurrency = balance
                .associateBy { it.currency }
                .map { it.key to it.value.actualAmount }
                .toMap()
        )
    }

}
