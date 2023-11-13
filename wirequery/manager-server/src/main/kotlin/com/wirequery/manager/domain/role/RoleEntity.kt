package com.wirequery.manager.domain.role

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("roles")
data class RoleEntity(
    @Id
    val id: Int? = null,
    val name: String,
    @MappedCollection(idColumn = "role_id")
    val authorisations: Set<RoleAuthorisation>,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @CreatedBy
    val createdBy: String? = null,
    @LastModifiedBy
    val updatedBy: String? = null,
) {
    @Table("role_authorisations")
    data class RoleAuthorisation(
        val name: String,
    )
}
