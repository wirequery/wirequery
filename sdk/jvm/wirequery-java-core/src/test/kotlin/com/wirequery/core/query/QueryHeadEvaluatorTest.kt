// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query

import com.wirequery.core.query.context.QueryHead
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class QueryHeadEvaluatorTest {
    @InjectMocks
    private lateinit var appHeadEvaluator: AppHeadEvaluator

    @CsvSource(
        "'',     '',     '',    'GET', '/abc', 200, true",
        "'POST', '',     '',    'GET', '/abc', 200, false",
        "'GET',  '',     '',    'GET', '/abc', 200, true",
        "'',     '/def', '',    'GET', '/abc', 200, false",
        "'',     '/abc', '',    'GET', '/abc', 200, true",
        "'',     '/{x}', '',    'GET', '/abc', 200, true",
        "'',     '/abc/{x}', '','GET', '/abc', 200, false",
        "'',     '',     '300', 'GET', '/abc', 200, false",
        "'',     '',     '3xx', 'GET', '/abc', 200, false",
        "'',     '',     '2xx', 'GET', '/abc', 200, true",
        "'',     '',     '200', 'GET', '/abc', 200, true",
    )
    @ParameterizedTest
    fun `evaluate returns matches as true if matching with request`(
        appHeadMethod: String,
        appHeadPath: String,
        appHeadStatusCode: String,
        method: String,
        path: String,
        statusCode: Int,
        expected: Boolean,
    ) {
        val queryHead = QueryHead(appHeadMethod, appHeadPath, appHeadStatusCode)
        val actual = appHeadEvaluator.evaluate(queryHead, method, path, statusCode)
        assertThat(actual.matches).isEqualTo(expected)
    }

    @Test
    fun `if paths match, the path variables are set`() {
        val queryHead = QueryHead("GET", "/abc/{def}", "")
        val actual = appHeadEvaluator.evaluate(queryHead, "GET", "/abc/xyz", 200)
        assertThat(actual.matches).isEqualTo(true)
        assertThat(actual.pathVariables).isEqualTo(mapOf("def" to "xyz"))
    }
}
