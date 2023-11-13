package com.wirequery.manager.domain.role

import com.wirequery.manager.domain.FunctionalException.Companion.checkFunctional
import com.wirequery.manager.domain.role.RoleEvent.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId

@Service
@Transactional
class RoleService(
    private val roleRepository: RoleRepository,
    private val publisher: ApplicationEventPublisher,
) {
    fun findById(id: Int): Role? {
        return roleRepository.findByIdOrNull(id)
            ?.let(::toDomainObject)
    }

    fun findByIds(ids: Iterable<Int>): List<Role> {
        if (ids.toList().isEmpty()) {
            return emptyList()
        }
        return roleRepository.findByIds(ids)
            .map(::toDomainObject)
    }

    fun findAll(): List<Role> {
        return roleRepository.findAll()
            .map(::toDomainObject)
    }

    fun findByNames(role: Set<String>): Set<Role> {
        if (role.isEmpty()) {
            return setOf()
        }
        return findAll()
            .filter { it.name in role }
            .toSet()
    }

    fun create(input: CreateRoleInput): Role {
        val roleEntity =
            RoleEntity(
                name = input.name,
                authorisations = input.authorisationNames.map { RoleEntity.RoleAuthorisation(it) }.toSet(),
            )
        val role = toDomainObject(roleRepository.save(roleEntity))
        publisher.publishEvent(RolesCreatedEvent(this, listOf(role)))
        return role
    }

    fun update(
        id: Int,
        input: UpdateRoleInput,
    ): Role? {
        val roleEntity = roleRepository.findByIdOrNull(id) ?: return null
        val role =
            roleRepository.save(
                roleEntity.copy(
                    name = input.name ?: roleEntity.name,
                    authorisations =
                        input.authorisationNames?.map { RoleEntity.RoleAuthorisation(it) }?.toSet()
                            ?: roleEntity.authorisations,
                ),
            ).let(::toDomainObject)
        publisher.publishEvent(RolesUpdatedEvent(this, listOf(role)))
        return role
    }

    fun deleteById(id: Int): Boolean {
        val role =
            roleRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return false
        roleRepository.deleteById(id)
        publisher.publishEvent(RolesDeletedEvent(this, listOf(role)))
        return true
    }

    private fun toDomainObject(entity: RoleEntity) =
        Role(
            id = entity.id!!,
            name = entity.name,
            authorisationNames = entity.authorisations.map { it.name },
            createdAt =
                entity.createdAt!!
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
            updatedAt =
                entity.updatedAt
                    ?.atZone(ZoneId.systemDefault())
                    ?.toOffsetDateTime(),
            createdBy = entity.createdBy,
            updatedBy = entity.updatedBy,
        )

    data class CreateRoleInput(
        val name: String,
        val authorisationNames: List<String>,
    ) {
        init {
            checkFunctional(name.isNotBlank()) { "Name is blank" }
            checkFunctional(!name.contains(",")) { "Name contains a comma" }
        }
    }

    data class UpdateRoleInput(
        val name: String?,
        val authorisationNames: List<String>?,
    ) {
        init {
            checkFunctional(name?.isNotBlank() ?: true) { "Name is blank" }
            checkFunctional(name?.contains(",") != true) { "Name contains a comma" }
        }
    }
}
