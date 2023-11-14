// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.querylog

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface QueryLogRepository : CrudRepository<QueryLogEntity, Unit> {
    @Query("SELECT * FROM query_logs WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<QueryLogEntity>

    @Query("SELECT * FROM query_logs WHERE stored_query_id IN (:storedQueryIds) AND main IS TRUE")
    fun findMainLogsByStoredQueryIds(
        @Param("storedQueryIds") storedQueryIds: Iterable<Int>,
    ): List<QueryLogEntity>

    @Query("SELECT * FROM query_logs WHERE stored_query_id = :storedQueryId AND trace_id = :traceId AND main IS FALSE")
    fun findNonMainByStoredQueryIdAndTraceId(
        @Param("storedQueryId") storedQueryId: Int,
        @Param("traceId") traceId: String,
    ): List<QueryLogEntity>
}
