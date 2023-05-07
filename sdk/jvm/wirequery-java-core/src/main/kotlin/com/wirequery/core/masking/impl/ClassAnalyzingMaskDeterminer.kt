package com.wirequery.core.masking.impl

class ClassAnalyzingMaskDeterminer(private val unmaskByDefault: Boolean) :
    com.wirequery.core.masking.ClassFieldMaskDeterminer {
    override fun shouldUnmask(value: Any, fieldName: String): Boolean {
        val clazz = value.javaClass
        if (clazz.annotations.any { it is com.wirequery.core.annotations.Mask } && clazz.annotations.any { it is com.wirequery.core.annotations.Unmask }) {
            error("Both @Mask and @Unmask annotations present on class ${clazz.name}")
        }
        var unmaskIfNoAnnotations = unmaskByDefault
        if (clazz.annotations.any { it is com.wirequery.core.annotations.Unmask }) {
            unmaskIfNoAnnotations = true
        }
        if (clazz.annotations.any { it is com.wirequery.core.annotations.Mask }) {
            unmaskIfNoAnnotations = false
        }
        val annotations = getAnnotationsByFieldName(fieldName, clazz)
        if (annotations.any { it is com.wirequery.core.annotations.Mask } && annotations.any { it is com.wirequery.core.annotations.Unmask }) {
            error("Both @Mask and @Unmask annotations present on field $fieldName")
        }
        if (annotations.any { it is com.wirequery.core.annotations.Unmask }) {
            return true
        }
        if (annotations.any { it is com.wirequery.core.annotations.Mask }) {
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
}
