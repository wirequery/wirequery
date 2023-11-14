// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.balancecalculator.wqextensions

import com.wirequery.core.masking.ObjectMasker
import com.wirequery.spring6.RequestData
import org.springframework.stereotype.Service

@Service
class OutboundTrafficExtender(
    private val requestData: RequestData,
    private val objectMasker: ObjectMasker
) {
    fun addOutboundRequest(name: String, url: String, requestBody: Any?, responseBody: Any?) {
        var outbound = requestData.extensions["outbound"] as MutableMap<String, MutableList<Any>>?
        if (outbound == null) {
            outbound = mutableMapOf()
            requestData.putExtension("outbound", outbound)
        }
        outbound
            .getOrPut(name) { mutableListOf() }
            .add(
                RequestResponse(
                    url = url,
                    requestBody = requestBody?.let(objectMasker::mask),
                    responseBody = responseBody?.let(objectMasker::mask)
                )
            )
    }

    data class RequestResponse(
        val url: String,
        val requestBody: Any?,
        val responseBody: Any?
    )
}
