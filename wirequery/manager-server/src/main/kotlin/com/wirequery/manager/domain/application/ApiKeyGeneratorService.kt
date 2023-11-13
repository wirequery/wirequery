package com.wirequery.manager.domain.application

import com.wirequery.manager.domain.tenant.TenantService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ApiKeyGeneratorService(private val tenantService: TenantService) {
    fun generateApiKey() = "${tenantService.tenantId}/${UUID.randomUUID()}"
}
