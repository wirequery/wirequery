// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wirequery.manager.domain.statistic.Statistic.TypeEnum
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDateTime

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
class StatisticService(
    private val statisticRepository: StatisticRepository,
    private val clock: Clock,
) {
    fun findAll(): List<Statistic> {
        return statisticRepository.findAll()
            .map(::toDomainObject)
    }

    @Async
    fun set(input: SetStatisticInput) {
        val moment = LocalDateTime.now(clock)
        statisticRepository.replace(
            moment = moment.toLocalDate(),
            hour = moment.hour,
            type = input.type.name,
            metadata = jacksonObjectMapper().writeValueAsString(input.metadata),
            amount = input.amount,
        )
    }

    @Async
    fun increment(input: IncrementStatisticInput) {
        val moment = LocalDateTime.now(clock)
        statisticRepository.incrementOrCreate(
            moment = moment.toLocalDate(),
            hour = moment.hour,
            type = input.type.name,
            metadata = jacksonObjectMapper().writeValueAsString(input.metadata),
            amount = input.amount,
        )
    }

    private fun toDomainObject(entity: StatisticEntity) =
        Statistic(
            id = entity.id!!,
            moment = entity.moment,
            hour = entity.hour,
            type = entity.type,
            metadata = entity.metadata,
            amount = entity.amount,
        )

    data class SetStatisticInput(
        val type: TypeEnum,
        val metadata: Map<String, String>,
        val amount: Int,
    )

    data class IncrementStatisticInput(
        val type: TypeEnum,
        val metadata: Map<String, String>,
        val amount: Int,
    )
}
