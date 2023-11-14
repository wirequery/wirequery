// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query

class QueryAuthorizer(
    private val allowedResources: Set<ResourceAuthorizationSetting>?,
    private val unallowedResources: Set<ResourceAuthorizationSetting>?
) {

    init {
        if (allowedResources != null && unallowedResources != null) {
            error("Both allowedResources and unallowedResources are set")
        }
    }

    fun isAuthorized(method: String, path: String): Boolean {
        val sanitizedMethod = method.trim().uppercase()
        if (allowedResources == null && unallowedResources == null) {
            return true
        }
        val pathWithoutQueryParams = path.split("?", limit = 2)[0]
        if (allowedResources?.any { pathMatches(path, it.path) } == true && method == "") {
            error("Method must be set in the query because this resource is restricted")
        }
        if (unallowedResources?.any { pathMatches(path, it.path) } == true && method == "") {
            error("Method must be set in the query because this resource is restricted")
        }
        if (allowedResources != null) {
            return matchesPathAndMethod(sanitizedMethod, pathWithoutQueryParams, allowedResources)
        }
        return !matchesPathAndMethod(method, pathWithoutQueryParams, unallowedResources!!)
    }

    private fun matchesPathAndMethod(method: String, path: String, resourceAuthorizationSetting: Set<ResourceAuthorizationSetting>) =
        resourceAuthorizationSetting.any {
            (it.methods == null || method in it.methods.map { m -> m.uppercase().trim() })
                    && pathMatches(path, it.path)
        }

    private fun pathMatches(path: String, pattern: String) = pattern
        .replace("**", ".*")
        .replace(PATH_VARIABLE_REGEX, "[^/]*")
        .toRegex().matches(path)

    data class ResourceAuthorizationSetting(
        val path: String,
        val methods: Set<String>?
    )

    private companion object {
        val PATH_VARIABLE_REGEX = "\\{\\w+}".toRegex()
        val ALLOWED_METHODS = setOf(
            "GET",
            "HEAD",
            "POST",
            "PUT",
            "DELETE",
            "CONNECT",
            "OPTIONS",
            "TRACE",
        )
    }
}
