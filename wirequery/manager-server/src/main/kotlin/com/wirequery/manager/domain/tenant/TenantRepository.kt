// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

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
