package com.balancecalculator.balance

import com.wirequery.core.annotations.Unmask

@Unmask
data class Balance(
    val balance: Int,
    val totalPerCurrency: Map<String, Int>
)
