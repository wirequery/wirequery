// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.application

import com.wirequery.manager.domain.tenant.TenantService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApiKeyGeneratorService(private val tenantService: TenantService) {
    fun generateApiKey() = "${tenantService.tenantId}/${UUID.randomUUID()}"
}
