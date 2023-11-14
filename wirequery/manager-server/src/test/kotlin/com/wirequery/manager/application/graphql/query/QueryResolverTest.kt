// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.query

import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.domain.query.QueryService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

// TODO test authorisations
@ExtendWith(MockitoExtension::class)
class QueryResolverTest : ResolverTestContext() {
    @Mock
    private lateinit var queryService: QueryService

    @InjectMocks
    private lateinit var queryResolver: QueryResolver

    // NOTE this class is manually tested, since the QueryResolver is short yet complex to test and mostly glue code.
    // Room for improvement, but not a high priority for now.
}
