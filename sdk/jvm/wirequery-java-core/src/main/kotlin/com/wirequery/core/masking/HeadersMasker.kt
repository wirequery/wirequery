package com.wirequery.core.masking

interface HeadersMasker {
    fun maskRequestHeaders(value: Map<String, List<String>>): Map<String, List<String>>
    fun maskResponseHeaders(value: Map<String, List<String>>): Map<String, List<String>>
}
