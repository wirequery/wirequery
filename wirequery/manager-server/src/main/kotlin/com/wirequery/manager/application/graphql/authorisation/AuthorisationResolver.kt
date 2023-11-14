// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.authorisation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.wirequery.manager.domain.authorisation.Authorisation
import com.wirequery.manager.domain.authorisation.AuthorisationService
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
@PreAuthorize("isAuthenticated()")
class AuthorisationResolver(
    private val authorisationService: AuthorisationService,
) {
    @DgsQuery
    fun authorisations(): Iterable<Authorisation> {
        return authorisationService.findAll()
    }
}
