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

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(ObjectWithAString(someValue = null)))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask for null`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker.mask(ObjectWithAString(someValue = null))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(ObjectWithAString(someValue = null)))
    }

    @Test
    fun `mask masks fields that are not marked as unmask`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(ObjectWithAString(someValue = "x"))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(ObjectWithAString(someValue = MASKING_LABEL)))
    }

    @Test
    fun `mask also works on arrays`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(arrayOf(ObjectWithAString(someValue = "x")))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(arrayOf(ObjectWithAString(someValue = MASKING_LABEL))))
    }

    @Test
    fun `mask also works on iterables`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(listOf(ObjectWithAString(someValue = "x")))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(listOf(ObjectWithAString(someValue = MASKING_LABEL))))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask for strings`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker.mask(ObjectWithAString(someValue = "x"))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(ObjectWithAString(someValue = "x")))
    }

    @Test
    fun `mask masks fields that are not marked as unmask for numbers`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithANumber>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(ObjectWithANumber(someValue = 1))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(ObjectWithAString(someValue = MASKING_LABEL)))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask for numbers`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithANumber>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker.mask(ObjectWithANumber(someValue = 1))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(ObjectWithANumber(someValue = 1)))
    }

    @Test
    fun `mask mask fields that are not marked as unmask when nested in an object`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker.mask(ObjectWithSubObject(someObject = ObjectWithAString(someValue = "x")))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(
                objectMapper.writeValueAsString(
                    ObjectWithSubObject(
                        someObject = ObjectWithAString(
                            someValue = MASKING_LABEL
                        )
                    )
                )
            )
    }

    @Test
    fun `mask does not mask fields that are marked as unmask when nested in an object`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker.mask(ObjectWithSubObject(someObject = ObjectWithAString(someValue = "x")))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(
                objectMapper.writeValueAsString(
                    ObjectWithSubObject(
                        someObject = ObjectWithAString(
                            someValue = "x"
                        )
                    )
                )
            )
    }

    @Test
    fun `mask masks fields that are not marked as unmask when nested in an array`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectArray(someObjects = arrayOf(ObjectWithAString(someValue = "x"))))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(
                objectMapper.writeValueAsString(
                    ObjectWithSubObjectArray(
                        someObjects = arrayOf(
                            ObjectWithAString(someValue = MASKING_LABEL)
                        ),
                    )
                )
            )
    }

    @Test
    fun `mask does not mask fields that are marked as unmask when nested in an array`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectArray(someObjects = arrayOf(ObjectWithAString(someValue = "x"))))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(
                objectMapper.writeValueAsString(
                    ObjectWithSubObjectArray(
                        someObjects = arrayOf(ObjectWithAString(someValue = "x")),
                    )
                )
            )
    }

    @Test
    fun `mask masks fields that are not marked as unmask when objects nested in a collection`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(false)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectList(someObjects = listOf(ObjectWithAString(someValue = "x"))))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(
                objectMapper.writeValueAsString(
                    ObjectWithSubObjectList(
                        someObjects = listOf(
                            ObjectWithAString(someValue = MASKING_LABEL)
                        ),
                    )
                )
            )
    }

    @Test
    fun `mask does not mask fields that are marked as unmask when objects nested in a collection`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithAString>(), eq("someValue")))
            .thenReturn(true)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectList(someObjects = listOf(ObjectWithAString(someValue = "x"))))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(
                objectMapper.writeValueAsString(
                    ObjectWithSubObjectList(
                        someObjects = listOf(ObjectWithAString(someValue = "x")),
                    )
                )
            )
    }

    @Test
    fun `mask masks fields that are not marked as unmask when values nested in a collection`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithList>(), eq("someValues")))
            .thenReturn(false)

        val actual = simpleObjectMasker
            .mask(ObjectWithList(someValues = listOf("x")))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(ObjectWithList(someValues = listOf(MASKING_LABEL))))
    }

    @Test
    fun `mask does not mask fields that are marked as unmask when values nested in a collection`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithList>(), eq("someValues")))
            .thenReturn(true)

        val actual = simpleObjectMasker
            .mask(ObjectWithList(someValues = listOf("x")))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(objectMapper.writeValueAsString(ObjectWithList(someValues = listOf("x"))))
    }

    @Test
    fun `mask masks fields that are not marked as unmask when object nested in a map`() {
        whenever(classFieldMaskDeterminer.shouldUnmask(any<ObjectWithSubObjectMap>(), eq("someObjects")))
            .thenReturn(false)

        val actual = simpleObjectMasker
            .mask(ObjectWithSubObjectMap(someObjects = mapOf("key" to ObjectWithAString(someValue = "x"))))

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(
                objectMapper.writeValueAsString(
                    ObjectWithSomeObjectsString(
                        someObjects = MASKING_LABEL,
                    )
                )
            )
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

        assertThat(objectMapper.writeValueAsString(actual))
            .isEqualTo(
                objectMapper.writeValueAsString(
                    ObjectWithSubObjectMap(
                        someObjects = mapOf("key" to ObjectWithAString(someValue = "x")),
                    )
                )
            )
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

    data class ObjectWithSomeObjectsString(
        val someObjects: String
    )
}
