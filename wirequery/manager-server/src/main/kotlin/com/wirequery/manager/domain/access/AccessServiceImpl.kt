// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.access

import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum
import org.springframework.stereotype.Service

@Service
class AccessServiceImpl : AccessService {
    override fun isExpressionAllowed(
        expression: String,
        groupAuthorisationEnum: GroupAuthorisationEnum,
    ): Boolean {
        return true
    }

    override fun isExpressionTemplateAllowed(
        expression: String,
        groupAuthorisationEnum: GroupAuthorisationEnum,
    ): Boolean {
        return true
    }

    override fun isAuthorisedByApplicationId(
        applicationId: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean {
        return false
    }

    override fun isAuthorisedByGroupUserId(
        groupUserId: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean {
        return false
    }

    override fun isAuthorisedByStoredQueryId(
        id: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean {
        return false
    }

    override fun isAuthorisedByGroupId(
        groupId: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean {
        return false
    }

    override fun isAuthorisedByGroupApplicationId(
        groupApplicationId: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean {
        return false
    }

    override fun isAuthorisedBySessionIds(
        sessionIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean {
        return false
    }

    override fun isAuthorisedByStoredQueryIds(
        storedQueryIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean {
        return false
    }

    override fun isAuthorisedByGroupUserIds(
        groupUserIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean {
        return false
    }

    override fun isAuthorisedByApplicationIds(
        applicationIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean {
        return false
    }

    override fun whichAuthorisedByApplicationId(
        applicationIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Set<Int> {
        return applicationIds
    }
}
