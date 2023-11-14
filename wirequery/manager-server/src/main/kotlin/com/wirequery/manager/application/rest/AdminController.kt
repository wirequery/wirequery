package com.wirequery.manager.application.rest

import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.role.RoleService
import com.wirequery.manager.domain.role.RoleService.CreateRoleInput
import com.wirequery.manager.domain.tenant.TenantRequestContext
import com.wirequery.manager.domain.tenant.TenantService
import com.wirequery.manager.domain.user.UserService
import com.wirequery.manager.domain.user.UserService.RegisterInput
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

// This class should NEVER be exposed to the outside world.
@ConditionalOnProperty("wirequery.admin.enabled", havingValue = "true")
@RestController
class AdminController(
    private val tenantService: TenantService,
    private val roleService: RoleService,
    private val userService: UserService,
    private val tenantRequestContext: TenantRequestContext,
) {
    @PostMapping("/api/internal/admin/new-env/{name}")
    fun admin(
        @PathVariable name: String,
    ) {
        tenantRequestContext.tenantId = 0
        tenantRequestContext.tenantId =
            tenantService.create(
                TenantService.CreateTenantInput(
                    name = name,
                    slug = name,
                    plan = "",
                    enabled = true,
                ),
            ).id

        roleService.create(CreateRoleInput("Administrator", AuthorisationEnum.entries.map { it.name }))
        userService.register(RegisterInput("admin", "Administrator", true, "Administrator"))
    }
}