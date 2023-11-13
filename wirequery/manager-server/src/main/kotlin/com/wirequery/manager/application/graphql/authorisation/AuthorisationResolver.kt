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
