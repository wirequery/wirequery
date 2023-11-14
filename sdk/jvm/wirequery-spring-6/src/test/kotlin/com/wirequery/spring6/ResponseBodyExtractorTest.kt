// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring6

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.converter.HttpMessageConverter

@ExtendWith(MockitoExtension::class)
internal class ResponseBodyExtractorTest {

    @Mock
    private lateinit var requestData: RequestData

    @InjectMocks
    private lateinit var responseBodyExtractor: ResponseBodyExtractor

    @Test
    fun `all requests are supported`() {
        assertThat(responseBodyExtractor.supports(mock(), HttpMessageConverter::class.java))
            .isEqualTo(true)
    }

    @Test
    fun `before the response body is written, it is stored in the requestData object and returned unchanged`() {
        val body = mock<Any>()
        val returnValue = responseBodyExtractor.beforeBodyWrite(body, mock(), mock(), HttpMessageConverter::class.java, mock(), mock())
        verify(requestData).responseBody = body
        assertThat(returnValue).isEqualTo(body)
    }
}
