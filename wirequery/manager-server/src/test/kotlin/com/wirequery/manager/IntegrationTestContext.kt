// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager

import com.wirequery.manager.application.db.TenantAwareDataSource
import com.wirequery.manager.domain.tenant.TenantRequestContext
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

@ExtendWith(SpringExtension::class)
@SpringBootTest
@Testcontainers
@Transactional // Rolls back transaction after each test.
@Configuration
@Import(IntegrationTestContext.IntegrationTestContextConfig::class)
abstract class IntegrationTestContext {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var tenantRequestContext: TenantRequestContext

    companion object {
        @JvmField
        val POSTGRES_CONTAINER: PostgreSQLContainer<Nothing> =
            PostgreSQLContainer<Nothing>(
                DockerImageName
                    .parse("timescale/timescaledb-ha:pg14-latest")
                    .asCompatibleSubstituteFor("postgres"),
            )
                .apply {
                    withDatabaseName("foo")
                    withUsername("foo")
                    withPassword("secret")
                }

        @DynamicPropertySource
        @JvmStatic
        fun databaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { POSTGRES_CONTAINER.jdbcUrl }
            registry.add("spring.datasource.username") { POSTGRES_CONTAINER.username }
            registry.add("spring.datasource.password") { POSTGRES_CONTAINER.password }
        }

        init {
            POSTGRES_CONTAINER.start()
        }
    }

    @TestConfiguration
    class IntegrationTestContextConfig {
        @Bean
        @Primary
        fun dataSource(tenantRequestContext: TenantRequestContext): DataSource {
            return TenantAwareDataSource(
                tenantRequestContext,
                DataSourceBuilder.create()
                    .username(POSTGRES_CONTAINER.username)
                    .password(POSTGRES_CONTAINER.password)
                    .driverClassName("org.postgresql.Driver")
                    .url(POSTGRES_CONTAINER.jdbcUrl)
                    .build()!!,
            )
        }
    }

    @PostConstruct
    fun postConstruct() {
        tenantRequestContext.tenantId = 0
    }

    @BeforeEach
    fun init() {
        jdbcTemplate.execute(
            """
            INSERT INTO tenants (id, name, slug, plan, enabled, created_at)
            VALUES (0, 'Tests', 'tests', 'PAID', true, NOW())
            """,
        )
    }
}
