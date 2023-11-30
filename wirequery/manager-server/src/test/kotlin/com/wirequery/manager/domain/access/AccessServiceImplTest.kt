// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.access

import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum.VIEW_GROUP
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AccessServiceImplTest {
    private val accessServiceImpl = AccessServiceImpl()

    @Test
    fun `isExpressionAllowed always returns true in CE`() {
        assertThat(accessServiceImpl.isExpressionAllowed("", VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isExpressionTemplateAllowed always returns true in CE`() {
        assertThat(accessServiceImpl.isExpressionTemplateAllowed("", VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorisedByApplicationId always returns true in CE`() {
        assertThat(accessServiceImpl.isAuthorisedByApplicationId(1, VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorisedByGroupUserId always returns true in CE`() {
        assertThat(accessServiceImpl.isAuthorisedByGroupUserId(1, VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorisedByStoredQueryId always returns true in CE`() {
        assertThat(accessServiceImpl.isAuthorisedByStoredQueryId(1, VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorisedByGroupId always returns true in CE`() {
        assertThat(accessServiceImpl.isAuthorisedByGroupId(1, VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorisedByGroupApplicationId always returns true in CE`() {
        assertThat(accessServiceImpl.isAuthorisedByGroupApplicationId(1, VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorisedBySessionIds always returns true in CE`() {
        assertThat(accessServiceImpl.isAuthorisedBySessionIds(setOf(1), VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorisedByStoredQueryIds always returns true in CE`() {
        assertThat(accessServiceImpl.isAuthorisedByStoredQueryIds(setOf(1), VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorisedByGroupUserIds always returns true in CE`() {
        assertThat(accessServiceImpl.isAuthorisedByGroupUserIds(setOf(1), VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `isAuthorisedByApplicationIds always returns true in CE`() {
        assertThat(accessServiceImpl.isAuthorisedByApplicationIds(setOf(1), VIEW_GROUP))
            .isEqualTo(true)
    }

    @Test
    fun `whichAuthorisedByApplicationId always returns its input in CE`() {
        assertThat(accessServiceImpl.whichAuthorisedByApplicationId(setOf(), VIEW_GROUP))
            .isEqualTo(setOf<Int>())
    }
}
