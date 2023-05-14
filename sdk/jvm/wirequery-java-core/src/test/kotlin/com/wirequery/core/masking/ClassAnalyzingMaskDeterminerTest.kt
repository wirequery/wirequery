package com.wirequery.core.masking

import com.wirequery.core.annotations.Mask
import com.wirequery.core.annotations.Unmask
import com.wirequery.core.masking.impl.ClassAnalyzingMaskDeterminer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ClassAnalyzingMaskDeterminerTest {

    private val determinerNoUnmaskByDefault =
        ClassAnalyzingMaskDeterminer(unmaskByDefault = false)

    private val determinerUnmaskByDefault =
        ClassAnalyzingMaskDeterminer(unmaskByDefault = true)

    @Test
    fun `shouldUnmask returns true if the field should be unmasked`() {
        assertThat(determinerNoUnmaskByDefault.shouldUnmask(shouldMaskCase1, "field"))
            .isEqualTo(false)
        assertThat(determinerNoUnmaskByDefault.shouldUnmask(shouldMaskCase2, "field"))
            .isEqualTo(false)
        assertThat(determinerNoUnmaskByDefault.shouldUnmask(shouldMaskCase3, "field"))
            .isEqualTo(false)
        assertThat(determinerNoUnmaskByDefault.shouldUnmask(shouldMaskCase4, "field"))
            .isEqualTo(false)
    }

    @Test
    fun `shouldUnmask returns false if the field should be unmasked`() {
        assertThat(determinerNoUnmaskByDefault.shouldUnmask(shouldUnmaskCase1, "field"))
            .isEqualTo(true)
        assertThat(determinerNoUnmaskByDefault.shouldUnmask(shouldUnmaskCase2, "field"))
            .isEqualTo(true)
        assertThat(determinerNoUnmaskByDefault.shouldUnmask(shouldUnmaskCase3, "field"))
            .isEqualTo(true)
        assertThat(determinerNoUnmaskByDefault.shouldUnmask(shouldUnmaskCase4, "field"))
            .isEqualTo(true)
    }

    @Test
    fun `shouldUnmask throws error if Unmask and Mask annotations are both present`() {
        val ex1 = assertThrows<IllegalStateException> {
            determinerNoUnmaskByDefault.shouldUnmask(errorCase1, "field")
        }
        val ex2 = assertThrows<IllegalStateException> {
            determinerNoUnmaskByDefault.shouldUnmask(errorCase2, "field")
        }
        val ex3 = assertThrows<IllegalStateException> {
            determinerNoUnmaskByDefault.shouldUnmask(errorCase3, "field")
        }
        val ex4 = assertThrows<IllegalStateException> {
            determinerNoUnmaskByDefault.shouldUnmask(errorCase4, "field")
        }
        assertThat(ex1.message).isEqualTo("Both @Mask and @Unmask annotations present on class ${ErrorCase1::class.java.name}")
        assertThat(ex2.message).isEqualTo("Both @Mask and @Unmask annotations present on field field")
        assertThat(ex3.message).isEqualTo("Both @Mask and @Unmask annotations present on field field")
        assertThat(ex4.message).isEqualTo("Both @Mask and @Unmask annotations present on field field")
    }

    @Test
    fun `shouldUnmask should unmask by default if unmaskByDefault is set to true`() {
        assertThat(determinerUnmaskByDefault.shouldUnmask(shouldUnmaskCase5, "field"))
            .isEqualTo(true)
    }

    @Test
    fun `shouldUnmask should mask if unmaskByDefault is set to true but the class is annotated with Mask`() {
        assertThat(determinerUnmaskByDefault.shouldUnmask(shouldMaskCase5, "field"))
            .isEqualTo(false)
    }

    @Unmask
    @Mask
    data class ErrorCase1(
        val field: String
    )

    data class ErrorCase2(
        @get:Unmask @get:Mask
        val field: String
    )

    data class ErrorCase3(
        @field:Unmask @field:Mask
        val field: String
    )

    data class ErrorCase4(
        @Unmask @Mask
        val field: String
    )

    data class ShouldMaskCase1(
        val field: String
    )

    @Unmask
    data class ShouldMaskCase2(
        @get:Mask
        val field: String
    )

    @Unmask
    data class ShouldMaskCase3(
        @field:Mask
        val field: String
    )

    @Unmask
    data class ShouldMaskCase4(
        @Mask
        val field: String
    )

    @Mask
    data class ShouldMaskCase5(
        val field: String
    )

    @Unmask
    data class ShouldUnmaskCase1(
        val field: String
    )

    data class ShouldUnmaskCase2(
        @get:Unmask
        val field: String
    )

    data class ShouldUnmaskCase3(
        @field:Unmask
        val field: String
    )

    data class ShouldUnmaskCase4(
        @Unmask
        val field: String
    )

    data class ShouldUnmaskCase5(
        val field: String
    )

    private companion object {
        val shouldMaskCase1 = ShouldMaskCase1("")
        val shouldMaskCase2 = ShouldMaskCase2("")
        val shouldMaskCase3 = ShouldMaskCase3("")
        val shouldMaskCase4 = ShouldMaskCase4("")
        val shouldMaskCase5 = ShouldMaskCase5("")

        val shouldUnmaskCase1 = ShouldUnmaskCase1("")
        val shouldUnmaskCase2 = ShouldUnmaskCase2("")
        val shouldUnmaskCase3 = ShouldUnmaskCase3("")
        val shouldUnmaskCase4 = ShouldUnmaskCase4("")
        val shouldUnmaskCase5 = ShouldUnmaskCase5("")

        val errorCase1 = ErrorCase1("")
        val errorCase2 = ErrorCase2("")
        val errorCase3 = ErrorCase3("")
        val errorCase4 = ErrorCase4("")
    }

}