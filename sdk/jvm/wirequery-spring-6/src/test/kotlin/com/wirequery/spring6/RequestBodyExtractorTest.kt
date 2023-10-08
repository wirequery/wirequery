package com.wirequery.spring6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.converter.HttpMessageConverter
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@ExtendWith(MockitoExtension::class)
internal class RequestBodyExtractorTest {

    @Mock
    private lateinit var requestData: RequestData

    @InjectMocks
    private lateinit var requestBodyExtractor: RequestBodyExtractor

    @BeforeEach
    fun init() {
        requestBodyExtractor.clock = Clock.fixed(Instant.ofEpochMilli(10), ZoneId.systemDefault());
    }

    @Test
    fun `all requests are supported`() {
        assertThat(requestBodyExtractor.supports(mock(), mock(), HttpMessageConverter::class.java))
            .isEqualTo(true)
    }

    @Test
    fun `before the request body is read, it is stored in the requestData object and returned unchanged`() {
        val body = mock<Any>()
        val returnValue =
            requestBodyExtractor.afterBodyRead(body, mock(), mock(), mock(), HttpMessageConverter::class.java)
        verify(requestData).requestBody = body
        assertThat(returnValue).isEqualTo(body)
    }

}
