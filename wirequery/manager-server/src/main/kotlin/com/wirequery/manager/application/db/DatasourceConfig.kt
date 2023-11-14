// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.db

import com.wirequery.manager.domain.tenant.TenantRequestContext
import org.flywaydb.core.api.callback.Callback
import org.flywaydb.core.api.callback.Context
import org.flywaydb.core.api.callback.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
class DatasourceConfig {
    @Bean
    @Profile("!test")
    fun config(
        tenantRequestContext: TenantRequestContext,
        @Value("\${wirequery.db.user}") user: String,
        @Value("\${wirequery.db.password}") password: String,
        @Value("\${wirequery.db.url}") url: String,
        ctx: ApplicationContext,
    ) = TenantAwareDataSource(
        tenantRequestContext,
        DataSourceBuilder.create()
            .username(user)
            .password(password)
            .driverClassName("org.postgresql.Driver")
            .url(url)
            .build(),
    )

    @Bean
    @Profile("!test")
    fun rlsCallback(
        dataSource: DataSource,
        ctx: ApplicationContext,
    ): FlywayConfigurationCustomizer {
        return FlywayConfigurationCustomizer { config ->
            config.callbacks(
                RowSecurityCallback(
                    dataSource,
                    ctx,
                ),
            )
        }
    }

    class RowSecurityCallback(
        private val dataSource: DataSource,
        private val ctx: ApplicationContext,
    ) : Callback {
        override fun supports(
            event: Event,
            ctx: Context,
        ): Boolean {
            return event == Event.AFTER_MIGRATE
        }

        override fun canHandleInTransaction(
            event: Event,
            ctx: Context,
        ): Boolean {
            return false
        }

        override fun handle(
            event: Event,
            context: Context,
        ) {
            try {
                logger.info("Checking whether row security is active...")
                JdbcTemplate(dataSource)
                    .query("SELECT row_security_active('users')") {
                        try {
                            if (!it.getBoolean("row_security_active")) {
                                logger.error(
                                    """Datasource has no row security enabled. Please create, and connect to, a user that has row security enabled.
                                   |
                                   |To create a new user with the correct privileges, you can create the user using the following queries:
                                   |
                                   |  -- Don't forget to replace this password.
                                   |  CREATE USER "wirequery-multitenant" WITH PASSWORD '...';
                                   |
                                   |  GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON ALL TABLES IN SCHEMA public TO "wirequery-multitenant";
                                   |  GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO "wirequery-multitenant";
                                   |  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON TABLES TO "wirequery-multitenant";
                                   |  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO "wirequery-multitenant";
                                   |
                                   |Don't forget to replace the password.""".trimMargin("|"),
                                )
                                SpringApplication.exit(ctx, ExitCodeGenerator { 1 })
                            }
                        } finally {
                            it.close()
                        }
                    }
                logger.info("Row security is active.")
            } catch (e: Exception) {
                logger.warn("Unable to determine whether the database user has row security.")
            }
        }

        override fun getCallbackName(): String {
            return "rowSecurityCallback"
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(DatasourceConfig::class.java)
    }
}
