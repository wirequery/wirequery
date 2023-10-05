package com.wirequery.spring6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@ExtendWith(MockitoExtension::class)
internal class RequestBodyAndTimeExtractorTest {

    @Mock
    private lateinit var requestData: RequestData

    @InjectMocks
    private lateinit var requestBodyAndTimeExtractor: RequestBodyAndTimeExtractor

    @BeforeEach
    fun init() {
        requestBodyAndTimeExtractor.clock = Clock.fixed(Instant.ofEpochMilli(10), ZoneId.systemDefault());
    }

    @Test
    fun `all requests are supported`() {
        assertThat(requestBodyAndTimeExtractor.supports(mock(), mock(), HttpMessageConverter::class.java))
            .isEqualTo(true)
    }

    @Test
    fun `the startTime is set before the body is read`() {
        val inputMessage = mock<HttpInputMessage>()
        val returnValue =
            requestBodyAndTimeExtractor.beforeBodyRead(inputMessage, mock(), mock(), HttpMessageConverter::class.java)
        verify(requestData, times(0)).requestBody = any()
        verify(requestData).startTime = 10
        assertThat(returnValue).isEqualTo(inputMessage)
    }

    @Test
    fun `before the request body is read, it is stored in the requestData object and returned unchanged`() {
        val body = mock<Any>()
        val returnValue =
            requestBodyAndTimeExtractor.afterBodyRead(body, mock(), mock(), mock(), HttpMessageConverter::class.java)
        verify(requestData).requestBody = body
        assertThat(returnValue).isEqualTo(body)
    }

    @Test
    fun `the startTime is set when there is an empty body`() {
        val body = mock<Any>()
        val returnValue =
            requestBodyAndTimeExtractor.handleEmptyBody(body, mock(), mock(), mock(), HttpMessageConverter::class.java)
        verify(requestData, times(0)).requestBody = any()
        verify(requestData).startTime = 10
        assertThat(returnValue).isEqualTo(body)
    }
}
