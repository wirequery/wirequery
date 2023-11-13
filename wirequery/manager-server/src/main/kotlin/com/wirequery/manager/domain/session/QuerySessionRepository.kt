package com.wirequery.manager.domain.session

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface QuerySessionRepository : CrudRepository<SessionEntity, Int> {
    @Query("SELECT * FROM sessions WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<SessionEntity>

    @Query("SELECT * FROM sessions WHERE draft = FALSE")
    fun findByNonDraft(): List<SessionEntity>

    @Query("SELECT * FROM sessions WHERE draft = TRUE AND created_at + '1 hour'::interval < NOW()")
    fun findDrafts(): List<SessionEntity>
}
