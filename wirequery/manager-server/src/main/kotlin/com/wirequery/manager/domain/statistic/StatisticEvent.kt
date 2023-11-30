// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.statistic

import org.springframework.context.ApplicationEvent

sealed class StatisticEvent(source: Any) : ApplicationEvent(source) {
    data class StatisticsCreatedOrUpdatedEvent(private val _source: Any, val entities: List<Statistic>) : StatisticEvent(_source)
}
