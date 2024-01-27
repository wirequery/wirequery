// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.example.basket.entry

import com.example.basket.product.ProductsClient
import com.example.basket.product.ProductsClient.Product
import org.springframework.stereotype.Service

@Service
class EntryService(
    private val productsClient: ProductsClient,
) {
    private val accountToProductIdToQuantity = mutableMapOf<String, MutableMap<String, Int>>()

    fun getEntries(accountId: String): List<Entry> {
        val products: Map<String, Product> =
            productsClient
                .getProducts(accountId)
                .associateBy { it.id }

        val basketEntries = accountToProductIdToQuantity[accountId] ?: mapOf()

        return basketEntries.entries
            .mapNotNull {
                val product =
                    products[it.key]
                        ?: return@mapNotNull null
                Entry(
                    id = product.id,
                    name = product.name,
                    quantity = it.value,
                    unitPrice = product.price,
                    totalPrice = product.price * it.value,
                )
            }
            .sortedBy { it.totalPrice }
    }

    fun addToBasket(
        accountId: String,
        productId: String,
        quantity: Int,
    ) {
        if (accountToProductIdToQuantity[accountId] == null) {
            accountToProductIdToQuantity[accountId] = mutableMapOf()
        }
        val currentQuantity = accountToProductIdToQuantity[accountId]!!.get(productId) ?: 0
        accountToProductIdToQuantity[accountId]!![productId] = currentQuantity + quantity
    }

    fun removeFromBasket(
        accountId: String,
        productId: String,
    ) {
        if (accountToProductIdToQuantity[accountId] == null) {
            return
        }
        accountToProductIdToQuantity[accountId]!! -= productId
    }
}
