package com.wirequery.core.masking.impl

import com.wirequery.core.annotations.Mask
import com.wirequery.core.annotations.Unmask
import com.wirequery.core.masking.ClassFieldMaskDeterminer

class ClassAnalyzingMaskDeterminer(
    private val unmaskByDefault: Boolean,
    private val additionalClasses: Map<String, AdditionalClass>
) : ClassFieldMaskDeterminer {
    override fun shouldUnmask(value: Any, fieldName: String): Boolean {
        val clazz = value.javaClass
        if (clazz.annotations.any { it is Mask }) {
            if (clazz.annotations.any { it is Unmask }) {
                error("Both @Mask and @Unmask annotations present on class ${clazz.name}")
            }
        }
        var unmaskIfNoAnnotations = unmaskByDefault
        if (clazz.annotations.any { it is Unmask }) {
            unmaskIfNoAnnotations = true
        }
        if (clazz.annotations.any { it is Mask }) {
            unmaskIfNoAnnotations = false
        }
        val annotations = getAnnotationsByFieldName(fieldName, clazz)
        if (annotations.any { it is Mask }) {
            if (annotations.any { it is Unmask }) {
                error("Both @Mask and @Unmask annotations present on field $fieldName")
            }
        }
        if (annotations.any { it is Unmask }) {
            return true
        }
        if (annotations.any { it is Mask }) {
            return false
        }
        if (additionalClasses[value::class.java.name]?.unmask != null) {
            if (additionalClasses[value::class.java.name]?.mask != null) {
                error("Class both masked and unmasked")
            }
        }
        if (additionalClasses[value::class.java.name]?.fields?.get(fieldName)?.unmask != null) {
            if (additionalClasses[value::class.java.name]?.fields?.get(fieldName)?.mask != null) {
                error("Field both masked and unmasked")
            }
        }
        if (additionalClasses[value::class.java.name]?.fields?.get(fieldName)?.unmask == true) {
            return true
        }
        if (additionalClasses[value::class.java.name]?.fields?.get(fieldName)?.mask == true) {
            return false
        }
        if (additionalClasses[value::class.java.name]?.unmask == true) {
            return true
        }
        if (additionalClasses[value::class.java.name]?.mask == true) {
            return false
        }
        return unmaskIfNoAnnotations
    }

    private fun getAnnotationsByFieldName(
        fieldName: String,
        clazz: Class<Any>
    ): Set<Annotation> {
        val getterName = "get" + fieldName.replaceFirstChar { it.uppercase() }

        val getterAnnotations = clazz.methods
            .filter { it.name == getterName }
            .flatMap { it.declaredAnnotations.toSet() }
            .toSet()

        val declaredAnnotations = clazz.declaredFields
            .filter { it.name == fieldName }
            .flatMap { it.declaredAnnotations.toSet() }
            .toSet()

        val constructorAnnotations = clazz.constructors
            .flatMap { it.parameters.toList() }
            .filter { it.name == fieldName }
            .flatMap { it.declaredAnnotations.toSet() }
            .toSet()

        return getterAnnotations + declaredAnnotations + constructorAnnotations
    }

    data class AdditionalClass(
        val mask: Boolean? = null,
        val unmask: Boolean? = null,
        val fields: Map<String, AdditionalField>
    )

    data class AdditionalField(
        val mask: Boolean? = null,
        val unmask: Boolean? = null,
    )
}
