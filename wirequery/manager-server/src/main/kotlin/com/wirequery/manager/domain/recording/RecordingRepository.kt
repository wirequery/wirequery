// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.recording

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface RecordingRepository : CrudRepository<RecordingEntity, Int> {
    @Query("SELECT * FROM recordings WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<RecordingEntity>

    @Query("SELECT * FROM recordings WHERE session_id IN (:sessionIds)")
    fun findBySessionIds(
        @Param("sessionIds") sessionIds: Iterable<Int>,
    ): List<RecordingEntity>

    @Query("SELECT * FROM recordings WHERE template_id IN (:templateIds)")
    fun findByTemplateIds(
        @Param("templateIds") templateIds: Iterable<Int>,
    ): List<RecordingEntity>
}
