package com.wirequery.core.masking

import com.wirequery.core.masking.MaskingConstants.MASKING_LABEL
import com.wirequery.core.masking.impl.SimpleHeadersMasker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class SimpleHeadersMaskerTest {
    @Mock
    private lateinit var headerMaskDeterminer: HeaderMaskDeterminer
    @InjectMocks
    private lateinit var simpleHeadersMasker: SimpleHeadersMasker

    @Test
    fun `maskRequestHeaders masks request headers that are not marked as unmask`() {
        whenever(headerMaskDeterminer.shouldUnmaskRequestHeader("x"))
            .thenReturn(false)
        assertThat(simpleHeadersMasker.maskRequestHeaders(mapOf("x" to listOf("y", "z"))))
            .isEqualTo(mapOf("x" to listOf(MASKING_LABEL, MASKING_LABEL)))
    }

    @Test
    fun `maskRequestHeaders does not mask request headers that are marked as unmask`() {
        whenever(headerMaskDeterminer.shouldUnmaskRequestHeader("x"))
            .thenReturn(true)
        assertThat(simpleHeadersMasker.maskRequestHeaders(mapOf("x" to listOf("y", "z"))))
            .isEqualTo(mapOf("x" to listOf("y", "z")))
    }

    @Test
    fun `maskResponseHeaders masks response headers that are not marked as unmask`() {
        whenever(headerMaskDeterminer.shouldUnmaskResponseHeader("x"))
            .thenReturn(false)
        assertThat(simpleHeadersMasker.maskResponseHeaders(mapOf("x" to listOf("y", "z"))))
            .isEqualTo(mapOf("x" to listOf(MASKING_LABEL, MASKING_LABEL)))
    }

    @Test
    fun `maskResponseHeaders does not mask response headers that are marked as unmask`() {
        whenever(headerMaskDeterminer.shouldUnmaskResponseHeader("x"))
            .thenReturn(true)
        assertThat(simpleHeadersMasker.maskResponseHeaders(mapOf("x" to listOf("y", "z"))))
            .isEqualTo(mapOf("x" to listOf("y", "z")))
    }

}
