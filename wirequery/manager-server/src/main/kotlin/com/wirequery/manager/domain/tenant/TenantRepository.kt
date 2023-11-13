package com.wirequery.manager.domain.tenant

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface TenantRepository : CrudRepository<TenantEntity, Int> {
    @Query("SELECT * FROM tenants WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<TenantEntity>

    fun findBySlug(
        @Param("slug") slug: String,
    ): TenantEntity?
}
