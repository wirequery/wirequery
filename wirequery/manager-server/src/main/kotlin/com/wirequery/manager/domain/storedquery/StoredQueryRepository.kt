package com.wirequery.manager.domain.storedquery

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface StoredQueryRepository : CrudRepository<StoredQueryEntity, Int> {
    @Query("SELECT * FROM stored_querys WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<StoredQueryEntity>

    @Query("SELECT * FROM stored_querys WHERE session_id IN (:sessionIds)")
    fun findBySessionIds(
        @Param("sessionIds") sessionIds: Iterable<Int?>,
    ): List<StoredQueryEntity>

    @Query("SELECT * FROM stored_querys WHERE application_id IN (:applicationIds)")
    fun findByApplicationIds(
        @Param("applicationIds") applicationIds: Iterable<Int>,
    ): List<StoredQueryEntity>

    @Query("SELECT * FROM stored_querys WHERE application_id IN (:applicationIds) AND disabled = false")
    fun findEnabledByApplicationIds(
        @Param("applicationIds") applicationIds: Iterable<Int>,
    ): List<StoredQueryEntity>

    fun findByName(name: String): StoredQueryEntity?

    @Query("SELECT * FROM stored_querys WHERE end_date < NOW() AND disabled = false")
    fun findEnabledOverdueQueries(): List<StoredQueryEntity>

    @Query("SELECT * FROM stored_querys WHERE session_id IS NOT NULL")
    fun findByHasSessionId(): List<StoredQueryEntity>

    @Query("SELECT * FROM stored_querys WHERE session_id IS NULL")
    fun findByHasNoSessionId(): List<StoredQueryEntity>
}
