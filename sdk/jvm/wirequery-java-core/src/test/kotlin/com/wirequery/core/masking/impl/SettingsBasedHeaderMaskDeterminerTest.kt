// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.masking.impl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SettingsBasedHeaderMaskDeterminerTest {
    @Test
    fun `shouldUnmaskRequestHeader returns true when unmaskByDefault is false and request header in requestHeaders`() {
        val determiner =
            SettingsBasedHeaderMaskDeterminer(
                SettingsBasedHeaderMaskDeterminer.MaskSettings(
                    unmaskByDefault = false,
                    requestHeaders = setOf("x"),
                    responseHeaders = setOf(),
                ),
            )

        assertThat(determiner.shouldUnmaskRequestHeader("x")).isEqualTo(true)
    }

    @Test
    fun `shouldUnmaskRequestHeader returns false when unmaskByDefault is true and request header in requestHeaders`() {
        val determiner =
            SettingsBasedHeaderMaskDeterminer(
                SettingsBasedHeaderMaskDeterminer.MaskSettings(
                    unmaskByDefault = true,
                    requestHeaders = setOf("x"),
                    responseHeaders = setOf(),
                ),
            )

        assertThat(determiner.shouldUnmaskRequestHeader("x")).isEqualTo(false)
    }

    @Test
    fun `shouldUnmaskRequestHeader returns false when unmaskByDefault is false and request header not in requestHeaders`() {
        val determiner =
            SettingsBasedHeaderMaskDeterminer(
                SettingsBasedHeaderMaskDeterminer.MaskSettings(
                    unmaskByDefault = false,
                    requestHeaders = setOf(),
                    responseHeaders = setOf(),
                ),
            )

        assertThat(determiner.shouldUnmaskRequestHeader("x")).isEqualTo(false)
    }

    @Test
    fun `shouldUnmaskRequestHeader returns true when unmaskByDefault is true and request header not in requestHeaders`() {
        val determiner =
            SettingsBasedHeaderMaskDeterminer(
                SettingsBasedHeaderMaskDeterminer.MaskSettings(
                    unmaskByDefault = true,
                    requestHeaders = setOf(),
                    responseHeaders = setOf(),
                ),
            )

        assertThat(determiner.shouldUnmaskRequestHeader("x")).isEqualTo(true)
    }

    @Test
    fun `shouldUnmaskResponseHeader returns true when unmaskByDefault is false and response header in responseHeaders`() {
        val determiner =
            SettingsBasedHeaderMaskDeterminer(
                SettingsBasedHeaderMaskDeterminer.MaskSettings(
                    unmaskByDefault = false,
                    requestHeaders = setOf(),
                    responseHeaders = setOf("x"),
                ),
            )

        assertThat(determiner.shouldUnmaskResponseHeader("x")).isEqualTo(true)
    }

    @Test
    fun `shouldUnmaskResponseHeader returns false when unmaskByDefault is true and response header in responseHeaders`() {
        val determiner =
            SettingsBasedHeaderMaskDeterminer(
                SettingsBasedHeaderMaskDeterminer.MaskSettings(
                    unmaskByDefault = true,
                    requestHeaders = setOf(),
                    responseHeaders = setOf("x"),
                ),
            )

        assertThat(determiner.shouldUnmaskResponseHeader("x")).isEqualTo(false)
    }

    @Test
    fun `shouldUnmaskResponseHeader returns false when unmaskByDefault is false and response header not in responseHeaders`() {
        val determiner =
            SettingsBasedHeaderMaskDeterminer(
                SettingsBasedHeaderMaskDeterminer.MaskSettings(
                    unmaskByDefault = false,
                    requestHeaders = setOf(),
                    responseHeaders = setOf(),
                ),
            )

        assertThat(determiner.shouldUnmaskResponseHeader("x")).isEqualTo(false)
    }

    @Test
    fun `shouldUnmaskResponseHeader returns true when unmaskByDefault is true and response header not in responseHeaders`() {
        val determiner =
            SettingsBasedHeaderMaskDeterminer(
                SettingsBasedHeaderMaskDeterminer.MaskSettings(
                    unmaskByDefault = true,
                    requestHeaders = setOf(),
                    responseHeaders = setOf(),
                ),
            )

        assertThat(determiner.shouldUnmaskResponseHeader("x")).isEqualTo(true)
    }
}
