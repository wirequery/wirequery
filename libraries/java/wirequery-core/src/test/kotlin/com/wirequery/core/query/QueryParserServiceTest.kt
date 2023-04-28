package com.wirequery.core.query

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class QueryParserServiceTest {

    private val queryParserService = QueryParser()

    @CsvSource(
        "''                                 , app, ,       ,     ,       ,      ,    ,",
        "200                                , app, ,    200,     ,       ,      ,    ,",
        "20x                                , app, ,    20x,     ,       ,      ,    ,",
        "GET                                , app, GET,    ,     ,       ,      ,    ,",
        "/abc                               , app,    ,    , /abc,       ,      ,    ,",
        "GET 200 /abc                       , app, GET, 200, /abc,       ,      ,    ,",
        "GET | filter false                 , app, GET,    ,     , filter, false,    ,",
        "| filter true | map responseBody   , app, ,       ,     , filter, true , map, responseBody",
        "| filter true | map s + 'string|'  , app, ,       ,     , filter, true , map, s + 'string|'",
        "| filter true | map s + 'string\"|', app, ,       ,     , filter, true , map, s + 'string\"|'",
        "| filter true | map \"string|\"    , app, ,       ,     , filter, true , map, \"string|\"",
        "| filter true | map \"string'|\"   , app, ,       ,     , filter, true , map, \"string'|\"",
        "| filter true | map 'string\\'|'   , app, ,       ,     , filter, true , map, 'string\\'|'",
        "| filter true | map \"string|\\\"\", app, ,       ,     , filter, true , map, \"string|\\\"\"",
        "| filter true || false             , app, ,       ,     , filter, true || false,,",
    )
    @ParameterizedTest
    fun `the expression is split up into parts that are easy to compile`(
        expression: String,
        appName: String?,
        method: String?,
        statusCode: String?,
        path: String?,
        function1: String?,
        expression1: String?,
        function2: String?,
        expression2: String?
    ) {
        val result = queryParserService.parse(expression)
        assertThat(result.appHead.method).isEqualTo(method ?: "")
        assertThat(result.appHead.statusCode).isEqualTo(statusCode ?: "")
        assertThat(result.appHead.path).isEqualTo(path ?: "")
        assertThat(result.streamOperations.getOrNull(0)?.name ?: "").isEqualTo(function1 ?: "")
        assertThat(result.streamOperations.getOrNull(0)?.celExpression ?: "").isEqualTo(expression1 ?: "")
        assertThat(result.streamOperations.getOrNull(1)?.name ?: "").isEqualTo(function2 ?: "")
        assertThat(result.streamOperations.getOrNull(1)?.celExpression ?: "").isEqualTo(expression2 ?: "")
        assertThat(result.aggregatorOperation?.name).isNull()
    }

    @Test
    fun `if the last non-head stream part is an aggregator expression, it is stored as an aggregatorOperation`() {
        val queryParserService = QueryParser()
        val expression = "200 | map true | distinct"
        val result = queryParserService.parse(expression)
        assertThat(result.streamOperations.size).isEqualTo(1)
        assertThat(result.streamOperations[0].name).isEqualTo("map")
        assertThat(result.streamOperations[0].celExpression).isEqualTo("true")
        assertThat(result.aggregatorOperation?.name).isEqualTo("distinct")
        assertThat(result.aggregatorOperation?.celExpression).isEqualTo(null)
    }

    @CsvSource(
        "GET POST,  'Method is already set'",
        "/abc /def, 'Path is already set'",
        "2xx 3xx,   'Status code is already set'"
    )
    @ParameterizedTest
    fun `an exception is thrown for duplicate app head attributes`(expression: String, expectedMessage: String) {
        val exception = assertThrows<RuntimeException> {
            queryParserService.parse(expression)
        }
        assertThat(exception.message).isEqualTo(expectedMessage)
    }
}
