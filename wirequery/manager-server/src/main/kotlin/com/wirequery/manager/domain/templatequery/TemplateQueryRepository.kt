// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.templatequery

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface TemplateQueryRepository : CrudRepository<TemplateQueryEntity, Int> {
    @Query("SELECT * FROM template_querys WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<TemplateQueryEntity>

    @Query("SELECT * FROM template_querys WHERE template_id IN (:templateIds)")
    fun findByTemplateIds(
        @Param("templateIds") templateIds: Iterable<Int>,
    ): List<TemplateQueryEntity>

    @Query("SELECT * FROM template_querys WHERE application_id IN (:applicationIds)")
    fun findByApplicationIds(
        @Param("applicationIds") applicationIds: Iterable<Int>,
    ): List<TemplateQueryEntity>
}
