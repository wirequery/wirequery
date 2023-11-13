package com.wirequery.manager.domain.tenant

import com.wirequery.manager.domain.tenant.TenantEvent.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId

@Service
@Transactional
class TenantService(
    private val tenantRepository: TenantRepository,
    private val tenantRequestContext: TenantRequestContext,
    private val publisher: ApplicationEventPublisher,
) {
    var tenantId: Int
        get() = tenantRequestContext.tenantId
        set(id) {
            tenantRequestContext.tenantId = id
        }

    fun findById(id: Int): Tenant? {
        return tenantRepository.findByIdOrNull(id)
            ?.let(::toDomainObject)
    }

    fun findBySlug(slug: String): Tenant? {
        return tenantRepository.findBySlug(slug)
            ?.let(::toDomainObject)
    }

    fun findByIds(ids: Iterable<Int>): List<Tenant> {
        return tenantRepository.findByIds(ids)
            .map(::toDomainObject)
    }

    fun findAll(): List<Tenant> {
        return tenantRepository.findAll()
            .map(::toDomainObject)
    }

    fun create(input: CreateTenantInput): Tenant {
        val tenantEntity =
            TenantEntity(
                name = input.name,
                slug = input.slug,
                plan = input.plan,
                enabled = input.enabled,
            )
        val tenant = toDomainObject(tenantRepository.save(tenantEntity))
        publisher.publishEvent(TenantsCreatedEvent(this, listOf(tenant)))
        return tenant
    }

    fun update(
        id: Int,
        input: UpdateTenantInput,
    ): Tenant? {
        val tenantEntity = tenantRepository.findByIdOrNull(id) ?: return null
        val tenant =
            tenantRepository.save(
                tenantEntity.copy(
                    name = input.name ?: tenantEntity.name,
                    slug = input.slug ?: tenantEntity.slug,
                    plan = input.plan ?: tenantEntity.plan,
                    enabled = input.enabled ?: tenantEntity.enabled,
                ),
            ).let(::toDomainObject)
        publisher.publishEvent(TenantsUpdatedEvent(this, listOf(tenant)))
        return tenant
    }

    fun deleteById(id: Int): Boolean {
        val tenant =
            tenantRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return false
        tenantRepository.deleteById(id)
        publisher.publishEvent(TenantsDeletedEvent(this, listOf(tenant)))
        return true
    }

    private fun toDomainObject(entity: TenantEntity) =
        Tenant(
            id = entity.id!!,
            name = entity.name,
            slug = entity.slug,
            plan = entity.plan,
            enabled = entity.enabled,
            createdAt =
                entity.createdAt!!
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
            updatedAt =
                entity.updatedAt
                    ?.atZone(ZoneId.systemDefault())
                    ?.toOffsetDateTime(),
        )

    data class CreateTenantInput(
        val name: String,
        val slug: String,
        val plan: String,
        val enabled: Boolean,
    )

    data class UpdateTenantInput(
        val name: String?,
        val slug: String?,
        val plan: String?,
        val enabled: Boolean?,
    )
}
