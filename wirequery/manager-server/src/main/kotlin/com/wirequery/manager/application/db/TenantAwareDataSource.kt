// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.db

import com.wirequery.manager.domain.tenant.TenantRequestContext
import org.springframework.beans.factory.support.ScopeNotActiveException
import org.springframework.jdbc.datasource.ConnectionProxy
import org.springframework.jdbc.datasource.DelegatingDataSource
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.sql.Connection
import javax.sql.DataSource

class TenantAwareDataSource(
    private val tenantRequestContext: TenantRequestContext,
    targetDataSource: DataSource,
) : DelegatingDataSource(targetDataSource) {
    override fun getConnection(): Connection {
        val connection = targetDataSource!!.connection
        setTenantId(connection)
        return getTenantAwareConnectionProxy(connection)
    }

    override fun getConnection(
        username: String,
        password: String,
    ): Connection {
        val connection = targetDataSource!!.getConnection(username, password)
        setTenantId(connection)
        return getTenantAwareConnectionProxy(connection)
    }

    private fun setTenantId(connection: Connection) {
        val tenantId =
            try {
                tenantRequestContext.tenantId
            } catch (e: ScopeNotActiveException) {
                null
            }
        // Note that tenant id MUST be an int or sanitized to avoid SQL injections.
        connection.createStatement().use { sql ->
            sql.execute("SET app.tenant_id TO '$tenantId'")
        }
    }

    private fun clearTenantId(connection: Connection) {
        connection.createStatement().use { sql -> sql.execute("RESET app.tenant_id") }
    }

    private fun getTenantAwareConnectionProxy(connection: Connection): Connection {
        return Proxy.newProxyInstance(
            ConnectionProxy::class.java.classLoader,
            arrayOf<Class<*>>(ConnectionProxy::class.java),
            TenantAwareInvocationHandler(connection),
        ) as Connection
    }

    private inner class TenantAwareInvocationHandler(target: Connection) : InvocationHandler {
        private val target: Connection

        init {
            this.target = target
        }

        override fun invoke(
            proxy: Any?,
            method: Method,
            args: Array<Any?>?,
        ): Any? {
            return when (method.name) {
                "equals" -> proxy === args?.get(0)
                "hashCode" -> System.identityHashCode(proxy)
                "unwrap" ->
                    if ((args?.get(0) as Class<*>).isInstance(proxy)) {
                        proxy
                    } else {
                        method.invoke(target, *args)
                    }

                "isWrapperFor" ->
                    if ((args?.get(0) as Class<*>).isInstance(proxy)) {
                        true
                    } else {
                        method.invoke(target, *args)
                    }

                "getTargetConnection" -> target
                else -> {
                    if (method.name.equals("close")) {
                        clearTenantId(target)
                    }
                    method.invoke(target, *(args ?: arrayOf()))
                }
            }
        }
    }
}
