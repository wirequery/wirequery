// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class RequestDataTest {
    @InjectMocks
    private lateinit var requestData: RequestData

    @Test
    fun `putExtension attaches an extension to the RequestData field`() {
        requestData.putExtension("extension", SOME_EXTENSION)

        assertThat(requestData.extensions["extension"]).isEqualTo(SOME_EXTENSION)
    }

    @Test
    fun `putExtension throws an exception when a key is already set`() {
        requestData.putExtension("extension", SOME_EXTENSION)
        val exception =
            assertThrows<IllegalStateException> {
                requestData.putExtension("extension", SOME_EXTENSION)
            }

        assertThat(exception.message).isEqualTo("extension is already set")
    }

    private companion object {
        const val SOME_EXTENSION = "some-extension"
    }
}
