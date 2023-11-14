// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.balancecalculator.clients

import com.balancecalculator.wqextensions.OutboundTrafficExtender
import com.wirequery.core.annotations.Unmask
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class TransactionsClient(
    private val outboundTrafficExtender: OutboundTrafficExtender,
    private val restTemplateBuilder: RestTemplateBuilder
) {

    fun getTransactions(accountId: String): List<Transaction> {
        val client = restTemplateBuilder.build()
        val url = "http://localhost:9101/transactions"
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Content-Type", "application/json")
        headers.add("AccountId", accountId)

        val responseBody = client.exchange<List<Transaction>>(url, HttpMethod.GET, HttpEntity<List<Transaction>>(headers)).body
        outboundTrafficExtender.addOutboundRequest("transactions", url, null, responseBody)
        return responseBody ?: listOf()
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
