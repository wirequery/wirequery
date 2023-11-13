package com.wirequery.manager.domain.template

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface TemplateRepository : CrudRepository<TemplateEntity, Int> {
    @Query("SELECT * FROM templates WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<TemplateEntity>
}
