package com.wirequery.core.masking.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import com.wirequery.core.masking.MaskingConstants.MASKING_LABEL

class SimpleObjectMasker(
    private val objectMapper: ObjectMapper,
    private val classFieldMaskDeterminer: com.wirequery.core.masking.ClassFieldMaskDeterminer
) : com.wirequery.core.masking.ObjectMasker {

    override fun mask(value: Any): JsonNode {
        return when (val node = objectMapper.valueToTree<JsonNode>(value)) {
            is ObjectNode ->
                return maskObjectNode(value, node)
            is ArrayNode ->
                return JsonNodeFactory.instance.arrayNode().let { arrayNode ->
                    arrayNode.addAll(
                        when (value) {
                            is Iterable<*> ->
                                value.map { subNode -> subNode?.let { mask(it) } }
                            is Array<*> ->
                                value.map { subNode -> subNode?.let { mask(it) } }
                            else ->
                                error("Unsupported type for masking: ${value.javaClass.name}")
                        })
                }
            else ->
                node
        }
    }

    private fun mask(value: Any?, node: JsonNode): JsonNode {
        if (value == null) {
            return JsonNodeFactory.instance.nullNode()
        }
        return when (node) {
            is ObjectNode ->
                return maskObjectNode(value, node)
            else ->
                node
        }
    }

    private fun maskObjectNode(value: Any, node: ObjectNode): ObjectNode {
        val result = JsonNodeFactory.instance.objectNode()
        node.fieldNames().forEach { fieldName ->
            result.set<JsonNode>(
                fieldName,
                when (val subNode = node.get(fieldName)) {
                    is ArrayNode ->
                        maskArrayNodeField(value, fieldName, subNode)
                    is ValueNode ->
                        maskValueNodeField(subNode, value, fieldName)
                    is ObjectNode ->
                        maskObjectNodeField(value, fieldName, subNode)
                    else ->
                        error("Unknown type: $fieldName")
                }
            )
        }
        return result
    }

    private fun maskArrayNodeField(
        value: Any,
        fieldName: String,
        subNode: JsonNode
    ): ArrayNode = when (val fieldOnObject = findField(value, fieldName)) {
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
    ) = if (subNode is NullNode || classFieldMaskDeterminer.shouldUnmask(value, fieldName)) {
        subNode
    } else {
        TextNode(MASKING_LABEL)
    }

    private fun maskObjectNodeField(
        value: Any,
        fieldName: String,
        subNode: JsonNode
    ): JsonNode = when (val fieldOnObject = findField(value, fieldName)) {
        is Map<*, *> -> {
            if (classFieldMaskDeterminer.shouldUnmask(value, fieldName)) {
                JsonNodeFactory.instance.objectNode().also { on ->
                    fieldOnObject.forEach { k, v ->
                        on.set<JsonNode>("$k", mask(v, subNode["$k"]))
                    }
                }
            } else {
                TextNode(MASKING_LABEL)
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
    ) = JsonNodeFactory.instance.arrayNode().let { an ->
        an.addAll(fieldOnObject.mapIndexed { i, it ->
            if (subNode[i] is ValueNode && !classFieldMaskDeterminer.shouldUnmask(value, fieldName)) {
                TextNode(MASKING_LABEL)
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
