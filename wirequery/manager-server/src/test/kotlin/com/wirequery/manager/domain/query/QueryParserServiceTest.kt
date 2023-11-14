// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.query

import com.wirequery.manager.domain.FunctionalException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class QueryParserServiceTest {
    @CsvSource(
        "app                                    , app, ,       ,     ,       ,      ,    ,",
        "app 200                                , app, ,    200,     ,       ,      ,    ,",
        "app 20x                                , app, ,    20x,     ,       ,      ,    ,",
        "app GET                                , app, GET,    ,     ,       ,      ,    ,",
        "app /abc                               , app,    ,    , /abc,       ,      ,    ,",
        "app GET 200 /abc                       , app, GET, 200, /abc,       ,      ,    ,",
        "app | filter false                     , app, ,       ,     , filter, false,    ,",
        "app | filter true | map responseBody   , app, ,       ,     , filter, true , map, responseBody",
        "app | filter true | map s + 'string|'  , app, ,       ,     , filter, true , map, s + 'string|'",
        "app | filter true | map s + 'string\"|', app, ,       ,     , filter, true , map, s + 'string\"|'",
        "app | filter true | map \"string|\"    , app, ,       ,     , filter, true , map, \"string|\"",
        "app | filter true | map \"string'|\"   , app, ,       ,     , filter, true , map, \"string'|\"",
        "app | filter true | map 'string\\'|'   , app, ,       ,     , filter, true , map, 'string\\'|'",
        "app | filter true | map \"string|\\\"\", app, ,       ,     , filter, true , map, \"string|\\\"\"",
        "app | filter true || false             , app, ,       ,     , filter, true || false,,",
    )
    @ParameterizedTest
    fun `the expression is split up into the specified parts`(
        expression: String,
        appName: String?,
        method: String?,
        statusCode: String?,
        path: String?,
        function1: String?,
        expression1: String?,
        function2: String?,
        expression2: String?,
    ) {
        val queryParserService = QueryParserService()
        val result = queryParserService.parse(expression)
        assertThat(result.queryHead.appName).isEqualTo(appName ?: "")
        assertThat(result.queryHead.method).isEqualTo(method ?: "")
        assertThat(result.queryHead.statusCode).isEqualTo(statusCode ?: "")
        assertThat(result.queryHead.path).isEqualTo(path ?: "")
        assertThat(result.streamOperations.getOrNull(0)?.name ?: "").isEqualTo(function1 ?: "")
        assertThat(result.streamOperations.getOrNull(0)?.celExpression ?: "").isEqualTo(expression1 ?: "")
        assertThat(result.streamOperations.getOrNull(1)?.name ?: "").isEqualTo(function2 ?: "")
        assertThat(result.streamOperations.getOrNull(1)?.celExpression ?: "").isEqualTo(expression2 ?: "")
        assertThat(result.aggregatorOperation?.name).isNull()
    }

    @Test
    fun `if the last expression is an aggregator expression, it is stored as an aggregatorExpression`() {
        val queryParserService = QueryParserService()
        val expression = "app 200 | map true | count"
        val result = queryParserService.parse(expression)
        assertThat(result.streamOperations.size).isEqualTo(1)
        assertThat(result.streamOperations[0].name).isEqualTo("map")
        assertThat(result.streamOperations[0].celExpression).isEqualTo("true")
        assertThat(result.aggregatorOperation?.name).isEqualTo("count")
        assertThat(result.aggregatorOperation?.celExpression).isEqualTo(null)
    }

    @Test
    fun `if the path is set twice, a functional exception is thrown`() {
        val queryParserService = QueryParserService()
        val expression = "app 200 /xyz /xyz"
        val exception =
            assertThrows<FunctionalException> {
                queryParserService.parse(expression)
            }
        assertThat(exception.message).isEqualTo("Path already set in query.")
    }

    @Test
    fun `if the status code is set twice, a functional exception is thrown`() {
        val queryParserService = QueryParserService()
        val expression = "app 200 200"
        val exception =
            assertThrows<FunctionalException> {
                queryParserService.parse(expression)
            }
        assertThat(exception.message).isEqualTo("Status code already set in query.")
    }
}
