package com.wirequery.core.masking

import com.wirequery.core.annotations.Mask
import com.wirequery.core.annotations.Unmask
import com.wirequery.core.masking.impl.ClassAnalyzingMaskDeterminer
import com.wirequery.core.masking.impl.ClassAnalyzingMaskDeterminer.AdditionalClass
import com.wirequery.core.masking.impl.ClassAnalyzingMaskDeterminer.AdditionalField
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ClassAnalyzingMaskDeterminerTest {

    private val determinerNoUnmaskByDefault =
        ClassAnalyzingMaskDeterminer(unmaskByDefault = false, additionalClasses = mapOf())

    private val determinerUnmaskByDefault =
        ClassAnalyzingMaskDeterminer(unmaskByDefault = true, additionalClasses = mapOf())

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

    @ParameterizedTest
    @CsvSource(
        "true ,true ,     ,     ,     , false",
        "false,     ,true ,     ,     , true",
        "true ,     ,     ,true ,     , false",
        "false,     ,     ,     ,true , true",
        "true ,     ,true ,true ,     , false",
        "false,true ,     ,     ,true , true",
    )
    fun `shouldUnmask should apply masking based on the additionalClasses`(
        unmaskByDefault: Boolean,
        maskClass: Boolean?,
        unmaskClass: Boolean?,
        maskField: Boolean?,
        unmaskField: Boolean?,
        expectedShouldUnmask: Boolean
    ) {
        val determiner = ClassAnalyzingMaskDeterminer(
            unmaskByDefault = unmaskByDefault,
            additionalClasses = mapOf(
                AdditionalClass1::class.java.name to AdditionalClass(
                    mask = maskClass,
                    unmask = unmaskClass,
                    fields = mapOf(
                        "field" to AdditionalField(
                            mask = maskField,
                            unmask = unmaskField,
                        )
                    )
                )
            )
        )

        assertThat(determiner.shouldUnmask(additionalClass1, "field"))
            .isEqualTo(expectedShouldUnmask)
    }

    @ParameterizedTest
    @CsvSource(
        "true ,true ,     ,     , 'Class both masked and unmasked'",
        "     ,     ,true ,true , 'Field both masked and unmasked'",
    )
    fun `shouldUnmask throw error if there are conflicting additionalClasses settings`(
        maskClass: Boolean?,
        unmaskClass: Boolean?,
        maskField: Boolean?,
        unmaskField: Boolean?,
        expectedMessage: String
    ) {
        val determiner = ClassAnalyzingMaskDeterminer(
            unmaskByDefault = true,
            additionalClasses = mapOf(
                AdditionalClass1::class.java.name to AdditionalClass(
                    mask = maskClass,
                    unmask = unmaskClass,
                    fields = mapOf(
                        "field" to AdditionalField(
                            mask = maskField,
                            unmask = unmaskField,
                        )
                    )
                )
            )
        )

        val exception = assertThrows<IllegalStateException> {
            determiner.shouldUnmask(additionalClass1, "field")
        }
        assertThat(exception.message).isEqualTo(expectedMessage)
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

    data class AdditionalClass1(
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

        val additionalClass1 = AdditionalClass1("")
    }

}
