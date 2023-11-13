package com.wirequery.manager.domain.access

import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum
import org.springframework.stereotype.Service

@Service
interface AccessService {
    fun isExpressionAllowed(
        expression: String,
        groupAuthorisationEnum: GroupAuthorisationEnum,
    ): Boolean

    fun isExpressionTemplateAllowed(
        expression: String,
        groupAuthorisationEnum: GroupAuthorisationEnum,
    ): Boolean

    fun isAuthorisedByApplicationId(
        applicationId: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean

    fun isAuthorisedByGroupUserId(
        groupUserId: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean

    fun isAuthorisedByStoredQueryId(
        id: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean

    fun isAuthorisedByGroupId(
        groupId: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean

    fun isAuthorisedByGroupApplicationId(
        groupApplicationId: Int,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean

    fun isAuthorisedBySessionIds(
        sessionIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean

    fun isAuthorisedByStoredQueryIds(
        storedQueryIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean

    fun isAuthorisedByGroupUserIds(
        groupUserIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean

    fun isAuthorisedByApplicationIds(
        applicationIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Boolean

    fun whichAuthorisedByApplicationId(
        applicationIds: Set<Int>,
        groupAuthorisation: GroupAuthorisationEnum,
    ): Set<Int>
}
