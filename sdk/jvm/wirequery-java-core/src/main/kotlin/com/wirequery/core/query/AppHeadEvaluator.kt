package com.wirequery.core.query

import com.wirequery.core.query.context.AppHead

class AppHeadEvaluator {

    fun evaluate(appHead: AppHead, method: String, path: String, statusCode: Int): AppHeadEvaluationResult {
        if (appHead.method.isNotEmpty() && appHead.method.lowercase() != method.lowercase()) {
            return AppHeadEvaluationResult(false, mapOf())
        }
        val pathMatchResult = matchPaths(appHead.path, path)
        if (appHead.path.isNotEmpty() && pathMatchResult == null) {
            return AppHeadEvaluationResult(false, mapOf())
        }
        if (appHead.statusCode.isNotEmpty() && !statusCodeMatches(appHead.statusCode, statusCode)) {
            return AppHeadEvaluationResult(false, mapOf())
        }
        return AppHeadEvaluationResult(true, pathMatchResult ?: mapOf())
    }

    private fun matchPaths(matcher: String, actual: String): Map<String, String>? {
        if (matcher.isEmpty()) {
            return mapOf()
        }
        val matcherParts = matcher.split("/")
        val actualParts = actual.split("/")
        if (matcherParts.size != actualParts.size) {
            return null
        }
        val result = mutableMapOf<String, String>()
        matcherParts.forEachIndexed { i, s ->
            if (s.startsWith("{") && s.endsWith("}")) {
                result[s.substring(1, s.lastIndex)] = actualParts[i]
            } else if (s != actualParts[i]) {
                return null
            }
        }
        return result
    }

    private fun statusCodeMatches(matcher: String, actual: Int): Boolean {
        val actualStringCharArray = actual.toString().toCharArray()

        check(matcher.matches("[0-9x]{3}".toRegex()))
        check(actualStringCharArray.size == 3)

        matcher.forEachIndexed { i, matcherChar ->
            if (matcherChar != 'x' && matcherChar != actualStringCharArray[i]) {
                return false
            }
        }
        return true
    }

    data class AppHeadEvaluationResult(
        val matches: Boolean,
        val pathVariables: Map<String, String>,
    )
}
