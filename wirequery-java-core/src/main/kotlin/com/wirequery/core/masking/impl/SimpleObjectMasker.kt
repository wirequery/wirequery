package com.wirequery.core.masking.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import com.wirequery.core.masking.MaskingConstants.MASKING_LABEL

class SimpleObjectMasker(
    private val objectMapper: ObjectMapper,
    private val classFieldMaskDeterminer: com.wirequery.core.masking.ClassFieldMaskDeterminer
) : com.wirequery.core.masking.ObjectMasker {

    override fun mask(value: Any): Any? {
        // We use Jackson here as it makes it easier to traverse the values in an object.
        // May be taken out at some point to improve performance or reduce how much the project
        // relies on third-party dependencies.
        val node = objectMapper.valueToTree<JsonNode>(value)
        return mask(value, node)
    }

    private fun mask(value: Any?, node: JsonNode): Any? {
        if (value == null) {
            return null
        }
        return when (node) {
            is ObjectNode ->
                return maskObjectNode(value, node)
            is ArrayNode ->
                return mutableListOf<Any?>().also { arrayNode ->
                    arrayNode.addAll(
                        when (value) {
                            is Iterable<*> ->
                                value.mapIndexed { i, subNode -> subNode?.let { mask(it, node[i]) } }
                            is Array<*> ->
                                value.mapIndexed { i, subNode -> subNode?.let { mask(it, node[i]) } }
                            else ->
                                error("Unsupported type for masking: ${value.javaClass.name}")
                        })
                }
            else ->
                value
        }
    }

    private fun maskObjectNode(value: Any, node: ObjectNode): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        node.fieldNames().forEach { fieldName ->
            result[fieldName] = when (val subNode = node.get(fieldName)) {
                is ArrayNode ->
                    maskArrayNodeField(value, fieldName, subNode)
                is ValueNode ->
                    maskValueNodeField(subNode, value, fieldName)
                is ObjectNode ->
                    maskObjectNodeField(value, fieldName, subNode)
                else ->
                    error("Unknown type: $fieldName")
            }
        }
        return result.toMap()
    }

    private fun maskArrayNodeField(
        value: Any,
        fieldName: String,
        subNode: JsonNode
    ): List<Any?> = when (val fieldOnObject = findField(value, fieldName)) {
        is Iterable<*> ->
            maskIterableArrayNodeField(fieldOnObject, subNode, value, fieldName)
        is Array<*> ->
            maskIterableArrayNodeField(fieldOnObject.toList(), subNode, value, fieldName)
        else -> {
            error("Unable to find object that serialized to a list for field $fieldName.")
        }
    }

    private fun maskValueNodeField(
        subNode: JsonNode?,
        value: Any,
        fieldName: String
    ): Any? = if (subNode is NullNode) {
        null
    } else if (subNode is TextNode) {
        subNode.textValue() // TODO add test (e.g. for Enums)
    } else if (classFieldMaskDeterminer.shouldUnmask(value, fieldName)) {
        value.javaClass.methods.singleOrNull { it.name == "get" }
            ?.let { value.javaClass.getMethod("get").invoke(value, fieldName) }
            ?: value.javaClass.methods.singleOrNull {
                it.name == "get" + fieldName.replaceFirstChar { c -> c.uppercase() }
            }?.invoke(value)
            ?: error("Unable to extract $fieldName") // No getter or get<FieldName>. Simply convert it to a string and hope for the best.
    } else {
        MASKING_LABEL
    }

    private fun maskObjectNodeField(
        value: Any,
        fieldName: String,
        subNode: JsonNode
    ): Any? = when (val fieldOnObject = findField(value, fieldName)) {
        is Map<*, *> -> {
            if (classFieldMaskDeterminer.shouldUnmask(value, fieldName)) {
                mutableMapOf<String, Any?>().also { on ->
                    fieldOnObject.forEach { k, v ->
                        on["$k"] = mask(v, subNode["$k"])
                    }
                }.toMap()
            } else {
                MASKING_LABEL
            }
        }
        else ->
            mask(findField(value, fieldName), subNode)
    }

    private fun maskIterableArrayNodeField(
        fieldOnObject: Iterable<*>,
        subNode: JsonNode,
        value: Any,
        fieldName: String
    ): List<Any?> = mutableListOf<Any?>().also { an ->
        an.addAll(fieldOnObject.mapIndexed { i, it ->
            if (subNode[i] is ValueNode && !classFieldMaskDeterminer.shouldUnmask(value, fieldName)) {
                MASKING_LABEL
            } else {
                mask(it, subNode[i])
            }
        })
    }

    private fun findField(value: Any, fieldName: String): Any? {
        val getterName = "get" + fieldName.replaceFirstChar { it.uppercase() }
        if (getterName in value.javaClass.declaredMethods.map { it.name }) {
            return value.javaClass.getMethod(getterName).invoke(value)
        }
        if (value is Map<*, *>) {
            return value[fieldName]
        }
        error("Unable to determine the type for field name: $fieldName")
    }

}
