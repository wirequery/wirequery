package com.wirequery.core.masking

import com.fasterxml.jackson.databind.ObjectMapper
import com.wirequery.core.masking.MaskingConstants.MASKING_LABEL
import com.wirequery.core.masking.impl.SimpleObjectMasker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

internal class SimpleObjectMaskerTest {
    private val objectMapper = ObjectMapper()
    private lateinit var classFieldMaskDeterminer: ClassFieldMaskDeterminer
    private lateinit var simpleObjectMasker: SimpleObjectMasker

    @BeforeEach
    fun init() {
        classFieldMaskDeterminer = mock()
        simpleObjectMasker = SimpleObjectMasker(
            objectMapper = objectMapper,
            classFieldMaskDeterminer = classFieldMaskDeterminer
        )
    }

    @Test
    fun `mask does not mask fields that are not marked as unmask but are null`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(ObjectWithAString(someValue = null))

        assertThat(actual).isEqualTo(mapOf<String, Any?>("someValue" to null))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask for null`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker.mask(ObjectWithAString(someValue = null))

        assertThat(actual).isEqualTo(mapOf<String, Any?>("someValue" to null))
    }

    @Test
    fun `mask masks fields that are not marked as unmask`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(ObjectWithAString(someValue = "x"))

        assertThat(actual).isEqualTo(mapOf("someValue" to MASKING_LABEL))
    }

    @Test
    fun `mask also works on arrays`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(arrayOf(ObjectWithAString(someValue = "x")))

        assertThat(actual).isEqualTo(listOf(mapOf("someValue" to MASKING_LABEL)))
    }

    @Test
    fun `mask also works on iterables`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(listOf(ObjectWithAString(someValue = "x")))

        assertThat(actual).isEqualTo(listOf(mapOf("someValue" to MASKING_LABEL)))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask for strings`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker.mask(ObjectWithAString(someValue = "x"))

        assertThat(actual).isEqualTo(mapOf("someValue" to "x"))
    }

    @Test
    fun `mask masks fields that are not marked as unmask for numbers`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithANumber>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(ObjectWithANumber(someValue = 1))

        assertThat(actual).isEqualTo(mapOf("someValue" to MASKING_LABEL))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask for numbers`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithANumber>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker.mask(ObjectWithANumber(someValue = 1))

        assertThat(actual).isEqualTo(mapOf("someValue" to 1))
    }

    @Test
    fun `mask mask fields that are not marked as unmask when nested in an object`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(ObjectWithSubObject(someObject = ObjectWithAString(someValue = "x")))

        assertThat(actual).isEqualTo(mapOf("someObject" to mapOf("someValue" to MASKING_LABEL)))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask when nested in an object`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker.mask(ObjectWithSubObject(someObject = ObjectWithAString(someValue = "x")))

        assertThat(actual).isEqualTo(mapOf("someObject" to mapOf("someValue" to "x")))
    }

    @Test
    fun `mask masks fields that are not marked as unmask when nested in an array`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectArray(someObjects = arrayOf(ObjectWithAString(someValue = "x"))))

        assertThat(actual).isEqualTo(mapOf("someObjects" to listOf(mapOf("someValue" to MASKING_LABEL))))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask when nested in an array`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectArray(someObjects = arrayOf(ObjectWithAString(someValue = "x"))))

        assertThat(actual).isEqualTo(mapOf("someObjects" to listOf(mapOf("someValue" to "x"))))
    }

    @Test
    fun `mask masks fields that are not marked as unmask when objects nested in a collection`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectList(someObjects = listOf(ObjectWithAString(someValue = "x"))))

        assertThat(actual).isEqualTo(mapOf("someObjects" to listOf(mapOf("someValue" to MASKING_LABEL))))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask when objects nested in a collection`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectList(someObjects = listOf(ObjectWithAString(someValue = "x"))))

        assertThat(actual).isEqualTo(mapOf("someObjects" to listOf(mapOf("someValue" to "x"))))
    }

    @Test
    fun `mask masks fields that are not marked as unmask when values nested in a collection`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithList>(), eq("someValues")))
            .thenReturn(false)

        val actual = simpleObjectMasker
            .mask(ObjectWithList(someValues = listOf("x")))

        assertThat(actual).isEqualTo(mapOf("someValues" to listOf(MASKING_LABEL)))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask when values nested in a collection`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithList>(), eq("someValues")))
            .thenReturn(true)

        val actual = simpleObjectMasker
            .mask(ObjectWithList(someValues = listOf("x")))

        assertThat(actual).isEqualTo(mapOf("someValues" to listOf("x")))
    }

    @Test
    fun `mask masks fields that are not marked as unmask when object nested in a map`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithSubObjectMap>(), eq("someObjects")))
            .thenReturn(false)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectMap(someObjects = mapOf("key" to ObjectWithAString(someValue = "x"))))

        assertThat(actual).isEqualTo(mapOf("someObjects" to MASKING_LABEL))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask when object nested in a map`() {
        doReturn(true)
            .whenever(classFieldMaskDeterminer)
            .shouldUnmask(any<ObjectWithSubObjectMap>(), eq("someObjects"))

        doReturn(true)
            .whenever(classFieldMaskDeterminer)
            .shouldUnmask(any<ObjectWithAString>(), eq("someValue"))

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectMap(someObjects = mapOf("key" to ObjectWithAString(someValue = "x"))))

        assertThat(actual).isEqualTo(mapOf("someObjects" to mapOf("key" to mapOf("someValue" to "x"))))
    }

    data class ObjectWithANumber(
        val someValue: Int
    )

    data class ObjectWithAString(
        val someValue: String?
    )

    data class ObjectWithSubObject(
        val someObject: ObjectWithAString
    )

    class ObjectWithSubObjectArray(
        val someObjects: Array<ObjectWithAString>
    )

    data class ObjectWithList(
        val someValues: List<String>
    )

    data class ObjectWithSubObjectList(
        val someObjects: List<ObjectWithAString>
    )

    data class ObjectWithSubObjectMap(
        val someObjects: Map<String, ObjectWithAString>
    )
}
