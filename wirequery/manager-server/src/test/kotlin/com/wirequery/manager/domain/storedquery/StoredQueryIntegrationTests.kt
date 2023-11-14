// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.storedquery

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationRepository
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.CREATE_STORED_QUERY_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class StoredQueryIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var storedQueryService: StoredQueryService

    @Autowired
    private lateinit var applicationService: ApplicationService

    @Autowired
    private lateinit var applicationRepository: ApplicationRepository

    @Test
    fun `StoredQuerys cannot be created if related entities do not exist`() {
        assertThrows<RuntimeException> {
            storedQueryService.create(CREATE_STORED_QUERY_FIXTURE_1)
        }
        assertThat(storedQueryService.findAll()).isEmpty()
    }

    @Test
    fun `StoredQuerys can be created, updated, fetched and deleted`() {
        val application = applicationRepository.save(APPLICATION_ENTITY_FIXTURE_1)

        val storedQuery =
            storedQueryService.create(
                CREATE_STORED_QUERY_FIXTURE_1.copy(
                    query = application.name,
                ),
            )

        assertThat(storedQueryService.findAll()).isNotEmpty
        assertThat(storedQueryService.findById(storedQuery.id)).isNotNull
        assertThat(storedQueryService.findByIds(listOf(storedQuery.id))).isNotEmpty

        assertThat(storedQueryService.findAll(StoredQueryService.StoredQueryFilterInput(applicationId = application.id))).isNotEmpty
        assertThat(storedQueryService.findByApplicationIds(listOf(application.id!!))).isNotEmpty

        storedQueryService.deleteById(storedQuery.id)

        assertThat(storedQueryService.findAll()).isEmpty()
    }

    @Test
    fun `StoredQuerys are deleted when related Applications are deleted`() {
        val application = applicationRepository.save(APPLICATION_ENTITY_FIXTURE_1)

        val storedQuery =
            storedQueryService.create(
                CREATE_STORED_QUERY_FIXTURE_1.copy(
                    query = application.name,
                ),
            )

        applicationService.deleteById(storedQuery.applicationId)

        assertThat(storedQueryService.findAll()).isEmpty()
    }
}
