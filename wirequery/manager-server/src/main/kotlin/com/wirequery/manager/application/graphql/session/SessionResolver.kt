package com.wirequery.manager.application.graphql.session

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.wirequery.manager.domain.session.Session
import com.wirequery.manager.domain.session.SessionService
import com.wirequery.manager.domain.session.SessionService.CreateSessionInput
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
@PreAuthorize("isAuthenticated()")
class SessionResolver(
    private val sessionService: SessionService,
) {
    @DgsQuery
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_SESSIONS.name())")
    fun session(id: Int): Session? {
        return sessionService.findById(id)
    }

    @DgsQuery
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_SESSIONS.name())")
    fun sessions(): Iterable<Session> {
        return sessionService.findAll()
    }

    @DgsMutation
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).CREATE_SESSION.name())")
    fun createSession(input: CreateSessionInput): Session {
        return sessionService.create(input, false)
    }

    @DgsMutation
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).DELETE_SESSION.name())")
    fun deleteSession(id: Int): Boolean {
        return sessionService.deleteById(id)
    }
}
