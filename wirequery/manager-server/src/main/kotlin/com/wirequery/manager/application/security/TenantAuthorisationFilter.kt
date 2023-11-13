package com.wirequery.manager.application.security

import com.wirequery.manager.domain.tenant.TenantRequestContext
import com.wirequery.manager.domain.tenant.TenantService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class TenantAuthorisationFilter(
    private val tenantRequestContext: TenantRequestContext,
    private val tenantService: TenantService,
    @Value("\${wirequery.tenant-id:0}")
    private val defaultTenantId: Int,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        tenantRequestContext.tenantId = -1
        val host = request.getHeader("Host")
        val tenantName =
            if (host.endsWith(".wirequery.io")) {
                host.removeSuffix(".wirequery.io")
            } else {
                null
            }

        tenantRequestContext.tenantId = tenantName
            ?.let { tenantService.findBySlug(it) ?: error("Could not find tenant by slug $it") }
            ?.id
            ?: defaultTenantId

        val authentication = SecurityContextHolder.getContext().authentication
        val user = authentication?.principal as? CustomUserDetails
        val userTenantId = user?.tenantId
        if (user == null || tenantRequestContext.tenantId == userTenantId) {
            chain.doFilter(request, response)
        } else {
            response.status = 403
        }
    }
}
