package com.wirequery.manager.domain.application

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ApplicationRepository : CrudRepository<ApplicationEntity, Int> {
    @Query("SELECT * FROM applications WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<ApplicationEntity>

    @Query("SELECT * FROM applications WHERE name = :name")
    fun findByName(
        @Param("name") name: String,
    ): ApplicationEntity?
}
