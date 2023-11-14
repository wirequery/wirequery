// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.balancecalculator.balance

import com.wirequery.core.annotations.Unmask

@Unmask
data class Balance(
    val balance: Int,
    val totalPerCurrency: Map<String, Int>
)
