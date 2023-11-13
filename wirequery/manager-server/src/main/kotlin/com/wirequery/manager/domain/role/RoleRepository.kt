package com.wirequery.manager.domain.role

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface RoleRepository : CrudRepository<RoleEntity, Int> {
    @Query("SELECT * FROM roles WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<RoleEntity>
}
