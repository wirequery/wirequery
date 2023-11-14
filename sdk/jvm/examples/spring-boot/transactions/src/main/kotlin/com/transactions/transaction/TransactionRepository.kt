// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.transactions.transaction

import com.transactions.transaction.Transaction.TransactionType.CREDIT
import com.transactions.transaction.Transaction.TransactionType.DEBIT
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
            description = "Car repairs with some ducktape"
        ),
        Transaction(
            id = 2,
            amount = 500,
            actualAmount = 500,
            currency = "EUR",
            type = CREDIT,
            fromAccount = "NL69FAKE8085990849",
            toAccount = "NL96FAKE8599084908",
            description = "Allowance"
        ),
    )

    fun findAll(accountId: String): List<Transaction> {
        return transactions.filter { it.fromAccount == accountId || it.toAccount == accountId }
    }

}
