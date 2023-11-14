// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.transactions.transaction

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
