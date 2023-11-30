// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface StatisticRepository : CrudRepository<StatisticEntity, Long> {
    @Query("SELECT * FROM statistics WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Long>,
    ): List<StatisticEntity>

    @Modifying
    @Query(
        """
        INSERT INTO statistics (moment, hour, type, metadata, amount)
        VALUES (:moment, :hour, :type, :metadata, :amount)
        ON CONFLICT ON CONSTRAINT statistics_tenant_id_moment_hour_type_metadata_key
        DO UPDATE SET amount = excluded.amount + :amount
        """,
    )
    fun incrementOrCreate(
        @Param("moment") moment: LocalDate,
        @Param("hour") hour: Int,
        @Param("type") type: String,
        @Param("metadata") metadata: String,
        @Param("amount") amount: Int,
    ): Boolean

    @Modifying
    @Query(
        """
        INSERT INTO statistics (moment, hour, type, metadata, amount)
        VALUES (:moment, :hour, :type, :metadata, :amount)
        ON CONFLICT ON CONSTRAINT statistics_tenant_id_moment_hour_type_metadata_key
        DO UPDATE SET amount = :amount
        """,
    )
    fun replace(
        @Param("moment") moment: LocalDate,
        @Param("hour") hour: Int,
        @Param("type") type: String,
        @Param("metadata") metadata: String,
        @Param("amount") amount: Int,
    ): Boolean
}
