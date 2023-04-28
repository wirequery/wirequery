package com.wirequery.core.query

import com.fasterxml.jackson.databind.JsonNode
import com.wirequery.core.masking.HeadersMasker
import com.wirequery.core.masking.MaskingConstants.MASKING_LABEL
import com.wirequery.core.masking.ObjectMasker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class ContextMapCreatorTest {
    @Mock
    private lateinit var headersMasker: HeadersMasker
    @Mock
    private lateinit var objectMasker: ObjectMasker
    @InjectMocks
    private lateinit var contextMapCreator: ContextMapCreator

    @Test
    fun `createContextMap maps intercepted requests to a context map`() {
        assertThat(contextMapCreator.createContextMap(SOME_INTERCEPTED, SOME_APP_HEAD_EVALUATION_RESULT))
            .isEqualTo(SOME_CONTEXT_MAP)
    }

    @Test
    fun `createMaskedContextMap maps intercepted requests to a masked context map`() {
        doReturn(SOME_MASKED_REQUEST_BODY)
            .whenever(objectMasker).mask("requestBody")

        doReturn(SOME_MASKED_RESPONSE_BODY)
            .whenever(objectMasker).mask("responseBody")

        whenever(headersMasker.maskRequestHeaders(mapOf("c" to listOf("d"))))
            .thenReturn(mapOf("c" to listOf(MASKING_LABEL)))

        whenever(headersMasker.maskResponseHeaders(mapOf("e" to listOf("f"))))
            .thenReturn(mapOf("e" to listOf(MASKING_LABEL)))

        assertThat(contextMapCreator.createMaskedContextMap(SOME_INTERCEPTED, SOME_APP_HEAD_EVALUATION_RESULT))
            .isEqualTo(SOME_MASKED_CONTEXT_MAP)
    }

    private companion object {
        val SOME_APP_HEAD_EVALUATION_RESULT = AppHeadEvaluator.AppHeadEvaluationResult(
            matches = true,
            pathVariables = mapOf("a" to "b"),
        )

        val SOME_INTERCEPTED = QueryEvaluator.InterceptedRequestResponse(
            method = "GET",
            path = "/abc",
            statusCode = 200,
            queryParameters = mapOf("a" to listOf("b")),
            requestBody = "requestBody",
            responseBody = "responseBody",
            requestHeaders = mapOf("c" to listOf("d")),
            responseHeaders = mapOf("e" to listOf("f"))
        )

        val SOME_CONTEXT_MAP = mapOf(
            "method" to "GET",
            "path" to "/abc",
            "pathVariables" to mapOf("a" to "b"),
            "statusCode" to 200,
            "queryParameters" to mapOf("a" to listOf("b")),
            "requestBody" to "requestBody",
            "responseBody" to "responseBody",
            "requestHeaders" to mapOf("c" to listOf("d")),
            "responseHeaders" to mapOf("e" to listOf("f"))
        )

        val SOME_MASKED_REQUEST_BODY = mock<JsonNode>()
        val SOME_MASKED_RESPONSE_BODY = mock<JsonNode>()

        val SOME_MASKED_CONTEXT_MAP = mapOf(
            "method" to "GET",
            "path" to "/abc",
            "pathVariables" to mapOf("a" to "b"),
            "statusCode" to 200,
            "queryParameters" to mapOf("a" to listOf("b")),
            "requestBody" to SOME_MASKED_REQUEST_BODY,
            "responseBody" to SOME_MASKED_RESPONSE_BODY,
            "requestHeaders" to mapOf("c" to listOf(MASKING_LABEL)),
            "responseHeaders" to mapOf("e" to listOf(MASKING_LABEL))
        )
    }
}
