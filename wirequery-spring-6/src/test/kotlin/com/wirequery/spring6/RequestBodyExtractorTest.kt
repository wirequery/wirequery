package com.wirequery.spring6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter

@ExtendWith(MockitoExtension::class)
internal class RequestBodyExtractorTest {

    @Mock
    private lateinit var requestData: RequestData

    @InjectMocks
    private lateinit var requestBodyExtractor: RequestBodyExtractor

    @Test
    fun `all requests are supported`() {
        assertThat(requestBodyExtractor.supports(mock(), mock(), HttpMessageConverter::class.java))
            .isEqualTo(true)
    }

    @Test
    fun `nothing happens before the body is read`() {
        val inputMessage = mock<HttpInputMessage>()
        val returnValue =
            requestBodyExtractor.beforeBodyRead(inputMessage, mock(), mock(), HttpMessageConverter::class.java)
        verify(requestData, times(0)).requestBody = any()
        assertThat(returnValue).isEqualTo(inputMessage)
    }

    @Test
    fun `before the request body is read, it is stored in the requestData object and returned unchanged`() {
        val body = mock<Any>()
        val returnValue =
            requestBodyExtractor.afterBodyRead(body, mock(), mock(), mock(), HttpMessageConverter::class.java)
        verify(requestData).requestBody = body
        assertThat(returnValue).isEqualTo(body)
    }

    @Test
    fun `nothing happens when there is an empty body`() {
        val body = mock<Any>()
        val returnValue =
            requestBodyExtractor.handleEmptyBody(body, mock(), mock(), mock(), HttpMessageConverter::class.java)
        verify(requestData, times(0)).requestBody = any()
        assertThat(returnValue).isEqualTo(body)
    }
}
