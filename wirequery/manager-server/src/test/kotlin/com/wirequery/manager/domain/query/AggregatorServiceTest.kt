package com.wirequery.manager.domain.query

import com.wirequery.manager.domain.FunctionalException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class AggregatorServiceTest {
    @InjectMocks
    private lateinit var aggregatorService: AggregatorService

    @Test
    fun `create with null aggregator simply returns the input`() {
        val actual = aggregatorService.create(QUERY_WITHOUT_AGGREGATOR)
        assertThat(actual.apply(VALUE1)).isEqualTo(VALUE1)
        assertThat(actual.apply(VALUE1)).isEqualTo(VALUE1)
    }

    @Test
    fun `create with distinct aggregator returns the same result once`() {
        val actual =
            aggregatorService.create(
                QUERY_WITHOUT_AGGREGATOR.copy(
                    aggregatorOperation = QueryParserService.Operation(name = "distinct", celExpression = null),
                ),
            )
        assertThat(actual.apply(VALUE1)).isEqualTo(VALUE1)
        assertThat(actual.apply(VALUE1)).isEqualTo(null)
        assertThat(actual.apply(VALUE2)).isEqualTo(VALUE2)
    }

    @Test
    fun `create with unknown aggregator throws an exception`() {
        val exception =
            assertThrows<FunctionalException> {
                aggregatorService.create(
                    QUERY_WITHOUT_AGGREGATOR.copy(
                        aggregatorOperation =
                            QueryParserService.Operation(
                                name = "someUnknownFunction",
                                celExpression = null,
                            ),
                    ),
                )
            }
        assertThat(exception.message).isEqualTo("Unknown aggregator expression.")
    }

    private companion object {
        val VALUE1 =
            QueryReport(
                appName = "",
                queryId = "",
                message = "m1",
                startTime = 10L,
                endTime = 20L,
                traceId = "abc",
            )

        val VALUE2 =
            QueryReport(
                appName = "",
                queryId = "",
                message = "m2",
                startTime = 10L,
                endTime = 20L,
                traceId = "abc",
            )

        val QUERY_WITHOUT_AGGREGATOR =
            QueryParserService.Query(
                queryHead =
                    QueryParserService.QueryHead(
                        appName = "some-app-name",
                        method = "GET",
                        path = "/some/path",
                        statusCode = "200",
                        trace = false,
                    ),
                streamOperations = listOf(),
                aggregatorOperation = null,
            )
    }
}
