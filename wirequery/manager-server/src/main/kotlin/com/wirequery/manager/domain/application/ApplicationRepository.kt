// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

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
