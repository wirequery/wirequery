// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.query

import com.google.common.hash.Hashing
import com.wirequery.manager.domain.FunctionalException.Companion.functionalError
import com.wirequery.manager.domain.query.AggregatorService.Aggregator
import org.springframework.stereotype.Service

@Service
class AggregatorService {
    fun create(query: QueryParserService.Query): Aggregator {
        if (query.aggregatorOperation?.name == "distinct") {
            return object : Aggregator {
                private val previousValues: MutableSet<Int> = mutableSetOf()

                override fun apply(queryReport: QueryReport): QueryReport? {
                    val hash = safeHashCode(queryReport.message)
                    if (!previousValues.contains(hash)) {
                        previousValues += hash
                        return queryReport
                    }
                    return null
                }
            }
        } else if (query.aggregatorOperation == null) {
            return Aggregator { it }
        }
        functionalError("Unknown aggregator expression.")
    }

    private fun safeHashCode(message: String): Int {
        return Hashing.goodFastHash(64).hashBytes(message.toByteArray()).asInt()
    }

    fun interface Aggregator {
        fun apply(queryReport: QueryReport): QueryReport?
    }
}
