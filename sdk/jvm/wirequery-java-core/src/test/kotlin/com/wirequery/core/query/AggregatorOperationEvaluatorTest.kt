// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query

import com.wirequery.core.query.context.CompiledQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock

@ExtendWith(MockitoExtension::class)
internal class AggregatorOperationEvaluatorTest {
    @InjectMocks
    private lateinit var aggregatorOperationEvaluator: AggregatorOperationEvaluator

    @Test
    fun `empty aggregator returns input`() {
        val compiledQuery =
            CompiledQuery(
                queryHead = mock(),
                streamOperations = listOf(),
                aggregatorOperation =
                    CompiledQuery.CompiledOperation(
                        name = "distinct",
                        celExpression = null,
                    ),
            )
        val result = aggregatorOperationEvaluator.evaluate(compiledQuery, mapOf("it" to "something"))
        assertThat(result).isEqualTo(listOf("something"))
    }

    @Test
    fun `unknown aggregator throws error`() {
        val compiledQuery =
            CompiledQuery(
                queryHead = mock(),
                streamOperations = listOf(),
                aggregatorOperation =
                    CompiledQuery.CompiledOperation(
                        name = "iDontExist",
                        celExpression = null,
                    ),
            )
        val caught =
            assertThrows<IllegalStateException> {
                aggregatorOperationEvaluator.evaluate(compiledQuery, mapOf("it" to "something"))
            }
        assertThat(caught.message).isEqualTo("Unknown operation: iDontExist")
    }

    @Test
    fun `distinct only returns elements that did not occur before`() {
        val compiledQuery =
            CompiledQuery(
                queryHead = mock(),
                streamOperations = listOf(),
                aggregatorOperation =
                    CompiledQuery.CompiledOperation(
                        name = "distinct",
                        celExpression = null,
                    ),
            )
        var context = mapOf("it" to "something-1")
        val result1 = aggregatorOperationEvaluator.evaluate(compiledQuery, context)
        context = mapOf("it" to "something-2")
        val result2 = aggregatorOperationEvaluator.evaluate(compiledQuery, context)
        context = mapOf("it" to "something-1")
        val result3 = aggregatorOperationEvaluator.evaluate(compiledQuery, context)

        assertThat(result1).isEqualTo(listOf("something-1"))
        assertThat(result2).isEqualTo(listOf("something-2"))
        assertThat(result3).isEqualTo(emptyList<Any>())
    }

    @Test
    fun `distinct does not support a celExpression`() {
        val compiledQuery =
            CompiledQuery(
                queryHead = mock(),
                streamOperations = listOf(),
                aggregatorOperation =
                    CompiledQuery.CompiledOperation(
                        name = "distinct",
                        celExpression = mock(),
                    ),
            )
        val exception =
            assertThrows<IllegalStateException> {
                aggregatorOperationEvaluator.evaluate(compiledQuery, mapOf("it" to "it"))
            }
        assertThat(exception.message).isEqualTo("Cel expression not allowed for 'distinct'")
    }
}
