// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.tenant

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.tenant.TenantFixtures.CREATE_TENANT_FIXTURE_1
import com.wirequery.manager.domain.tenant.TenantFixtures.UPDATE_TENANT_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TenantIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var tenantService: TenantService

    @Test
    fun `Tenants can be created, updated, fetched and deleted`() {
        val startSize = tenantService.findAll().size

        var tenant = tenantService.create(CREATE_TENANT_FIXTURE_1)
        tenant = tenantService.update(tenant.id, UPDATE_TENANT_FIXTURE_1)!!

        assertThat(tenantService.findAll()).isNotEmpty
        assertThat(tenantService.findById(tenant.id)).isNotNull
        assertThat(tenantService.findByIds(listOf(tenant.id))).isNotEmpty

        assertThat(tenantService.findAll().size).isEqualTo(startSize + 1)

        tenantService.deleteById(tenant.id)

        assertThat(tenantService.findAll().size).isEqualTo(startSize)
    }
}
