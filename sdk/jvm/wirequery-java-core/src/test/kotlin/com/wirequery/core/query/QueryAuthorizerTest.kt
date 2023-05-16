package com.wirequery.core.query

import com.wirequery.core.query.QueryAuthorizer.ResourceAuthorizationSetting
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class QueryAuthorizerTest {
    @Test
    fun `QueryAuthorizer cannot be constructed when both allowedResources and unallowedResources are set`() {
        val exception = assertThrows<IllegalStateException> {
            QueryAuthorizer(setOf(), setOf())
        }
        assertThat(exception.message).isEqualTo("Both allowedResources and unallowedResources are set")
    }

    @Test
    fun `isAuthorized always returns true when neither allowedResources nor unallowedResources are set`() {
        assertThat(QueryAuthorizer(null, null).isAuthorized("GET", ""))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorized returns true when provided path is in allowedResources`() {
        val queryAuthorizer = QueryAuthorizer(setOf(ResourceAuthorizationSetting(path = "/abc", methods = null)), null)
        assertThat(queryAuthorizer.isAuthorized("GET", "/abc")).isTrue()
    }

    @Test
    fun `isAuthorized returns true when provided path is in unallowedResources`() {
        val queryAuthorizer = QueryAuthorizer(null, setOf(ResourceAuthorizationSetting(path = "/abc", methods = null)))
        assertThat(queryAuthorizer.isAuthorized("GET", "/abc")).isFalse()
    }

    @Test
    fun `isAuthorized returns true when provided path is not in allowedResources`() {
        val queryAuthorizer = QueryAuthorizer(setOf(ResourceAuthorizationSetting(path = "/abc", methods = null)), null)
        assertThat(queryAuthorizer.isAuthorized("GET", "/def")).isFalse()
    }

    @Test
    fun `isAuthorized returns true when provided path is not in unallowedResources`() {
        val queryAuthorizer = QueryAuthorizer(null, setOf(ResourceAuthorizationSetting(path = "/abc", methods = null)))
        assertThat(queryAuthorizer.isAuthorized("GET", "/def")).isTrue()
    }

    @Test
    fun `isAuthorized matches on method, true case`() {
        val allowedResources = setOf(
            ResourceAuthorizationSetting(
                path = "/abc",
                methods = setOf("GET")
            )
        )
        val queryAuthorizer = QueryAuthorizer(allowedResources, null)
        assertThat(queryAuthorizer.isAuthorized("GET", "/abc"))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorized matches on method, true case with lowercase method and padding`() {
        val allowedResources = setOf(
            ResourceAuthorizationSetting(
                path = "/abc",
                methods = setOf("GET")
            )
        )
        val queryAuthorizer = QueryAuthorizer(allowedResources, null)
        assertThat(queryAuthorizer.isAuthorized(" get ", "/abc"))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorized matches on method, false case`() {
        val allowedResources = setOf(
            ResourceAuthorizationSetting(
                path = "/abc",
                methods = setOf("POST")
            )
        )
        val queryAuthorizer = QueryAuthorizer(allowedResources, null)
        assertThat(queryAuthorizer.isAuthorized("GET", "/abc"))
            .isEqualTo(false)
    }

    @Test
    fun `isAuthorized allows wildcards, one asterisk`() {
        val allowedResources = setOf(
            ResourceAuthorizationSetting(
                path = "/abc/{def}",
                methods = null
            )
        )
        val queryAuthorizer = QueryAuthorizer(allowedResources, null)
        assertThat(queryAuthorizer.isAuthorized("GET", "/abc/def"))
            .isEqualTo(true)
        assertThat(queryAuthorizer.isAuthorized("GET", "/abc/def/ghi"))
            .isEqualTo(false)
    }

    @Test
    fun `isAuthorized allows wildcards, two asterisks`() {
        val allowedResources = setOf(
            ResourceAuthorizationSetting(
                path = "/abc/**",
                methods = null
            )
        )
        val queryAuthorizer = QueryAuthorizer(allowedResources, null)
        assertThat(queryAuthorizer.isAuthorized("GET", "/abc/def"))
            .isEqualTo(true)
        assertThat(queryAuthorizer.isAuthorized("GET", "/abc/def/ghi"))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorized ignores query params`() {
        val allowedResources = setOf(
            ResourceAuthorizationSetting(
                path = "/abc",
                methods = null
            )
        )
        val queryAuthorizer = QueryAuthorizer(allowedResources, null)
        assertThat(queryAuthorizer.isAuthorized("GET", "/abc?xyz"))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorized throws Exception when method is empty and methods are set for matching allowedResources`() {
        val queryAuthorizer = QueryAuthorizer(
            allowedResources = setOf(ResourceAuthorizationSetting(path = "/abc", methods = setOf())),
            unallowedResources = null
        )
        val exception = assertThrows<IllegalStateException> {
            queryAuthorizer.isAuthorized("", "/abc")
        }
        assertThat(exception.message).isEqualTo("Method must be set in the query because this resource is restricted")
    }

    @Test
    fun `isAuthorized throws Exception when method is empty and methods are set for matching unallowedResources`() {
        val queryAuthorizer = QueryAuthorizer(
            allowedResources = null,
            unallowedResources = setOf(ResourceAuthorizationSetting(path = "/abc", methods = setOf())),
        )
        val exception = assertThrows<IllegalStateException> {
            queryAuthorizer.isAuthorized("", "/abc")
        }
        assertThat(exception.message).isEqualTo("Method must be set in the query because this resource is restricted")
    }
}
