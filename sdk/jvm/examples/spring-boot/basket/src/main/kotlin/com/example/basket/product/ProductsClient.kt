// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.example.basket.product

import com.example.basket.wqextensions.OutboundTrafficExtender
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.exchange

@Service
class ProductsClient(
    private val outboundTrafficExtender: OutboundTrafficExtender,
    private val restTemplateBuilder: RestTemplateBuilder
) {

    fun getProducts(accountId: String): List<Product> {
        val client = restTemplateBuilder.build()
        val url = "http://localhost:9101/products"
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Content-Type", "application/json")

        val responseBody = client.exchange<List<Product>>(url, HttpMethod.GET, HttpEntity<List<Product>>(headers)).body
        outboundTrafficExtender.addOutboundRequest("products", url, null, responseBody)
        return responseBody ?: listOf()
    }

    data class Product(
        val id: String,
        val name: String,
        val price: Int
    )
}
