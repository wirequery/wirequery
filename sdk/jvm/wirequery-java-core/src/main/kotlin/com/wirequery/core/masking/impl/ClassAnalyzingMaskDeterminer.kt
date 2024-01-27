// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.masking.impl

import com.wirequery.core.annotations.Mask
import com.wirequery.core.annotations.Unmask
import com.wirequery.core.masking.ClassFieldMaskDeterminer

class ClassAnalyzingMaskDeterminer(
    private val unmaskByDefault: Boolean,
    private val additionalClasses: Map<String, AdditionalClass>,
) : ClassFieldMaskDeterminer {
    override fun shouldUnmask(
        value: Any,
        fieldName: String,
    ): Boolean {
        val fieldAnnotations = getAnnotationsByFieldName(fieldName, value.javaClass)

        verifyAnnotationsSetCorrectly(value.javaClass, fieldAnnotations, fieldName)
        verifyAdditionalClassesSetCorrectly(value, fieldName)

        return determineShouldUnmaskUsingAdditionalClasses(value, fieldName)
            ?: determineShouldUnmaskUsingAnnotations(fieldAnnotations, value.javaClass)
            ?: unmaskByDefault
    }

    private fun determineShouldUnmaskUsingAdditionalClasses(
        value: Any,
        fieldName: String,
    ) = additionalClasses[value::class.java.name]?.let { additionalClass ->
        when {
            additionalClass.fields[fieldName]?.unmask == true ->
                true

            additionalClass.fields[fieldName]?.mask == true ->
                false

            additionalClass.unmask == true ->
                true

            additionalClass.mask == true ->
                false

            else ->
                null
        }
    }

    private fun determineShouldUnmaskUsingAnnotations(
        fieldAnnotations: Set<Annotation>,
        clazz: Class<Any>,
    ) = when {
        fieldAnnotations.any { it is Unmask } ->
            true

        fieldAnnotations.any { it is Mask } ->
            false

        clazz.annotations.any { it is Unmask } ->
            true

        clazz.annotations.any { it is Mask } ->
            false

        else ->
            null
    }

    private fun verifyAdditionalClassesSetCorrectly(
        value: Any,
        fieldName: String,
    ) {
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
    }

    private fun verifyAnnotationsSetCorrectly(
        clazz: Class<Any>,
        annotations: Set<Annotation>,
        fieldName: String,
    ) {
        if (clazz.annotations.any { it is Mask }) {
            if (clazz.annotations.any { it is Unmask }) {
                error("Both @Mask and @Unmask annotations present on class ${clazz.name}")
            }
        }
        if (annotations.any { it is Mask }) {
            if (annotations.any { it is Unmask }) {
                error("Both @Mask and @Unmask annotations present on field $fieldName")
            }
        }
    }

    private fun getAnnotationsByFieldName(
        fieldName: String,
        clazz: Class<Any>,
    ): Set<Annotation> {
        val getterName = "get" + fieldName.replaceFirstChar { it.uppercase() }

        val getterAnnotations =
            clazz.methods
                .filter { it.name == getterName }
                .flatMap { it.declaredAnnotations.toSet() }
                .toSet()

        val declaredAnnotations =
            clazz.declaredFields
                .filter { it.name == fieldName }
                .flatMap { it.declaredAnnotations.toSet() }
                .toSet()

        val constructorAnnotations =
            clazz.constructors
                .flatMap { it.parameters.toList() }
                .filter { it.name == fieldName }
                .flatMap { it.declaredAnnotations.toSet() }
                .toSet()

        return getterAnnotations + declaredAnnotations + constructorAnnotations
    }

    data class AdditionalClass(
        val mask: Boolean? = null,
        val unmask: Boolean? = null,
        val fields: Map<String, AdditionalField>,
    )

    data class AdditionalField(
        val mask: Boolean? = null,
        val unmask: Boolean? = null,
    )
}
