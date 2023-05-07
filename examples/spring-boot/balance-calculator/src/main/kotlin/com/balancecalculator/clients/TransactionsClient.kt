package com.balancecalculator.clients

import com.wirequery.core.annotations.Unmask
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@Service
class TransactionsClient {
    private val client = RestTemplate()

    fun getTransactions(accountId: String): List<Transaction> {
        return client.getForEntity<List<Transaction>>("http://localhost:9101/transactions").body
            ?: listOf()
    }

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
}
