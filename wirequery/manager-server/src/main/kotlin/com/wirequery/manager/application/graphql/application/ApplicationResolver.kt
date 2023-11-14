package com.wirequery.manager.application.graphql.application

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.wirequery.manager.domain.application.Application
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.application.ApplicationService.*
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class ApplicationResolver(
    private val applicationService: ApplicationService,
) {
    @DgsQuery
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_APPLICATIONS)
               || @accessService.isAuthorisedByApplicationId(#id, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_APPLICATION)""",
    )
    fun application(id: Int): Application? {
        return applicationService.findById(id)
    }

    @DgsMutation
    @PreAuthorize(
        "@accessService.isAuthorisedByApplicationId(#id, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_API_KEY)",
    )
    fun revealApiKey(id: Int): String? {
        return applicationService.findApiKeyById(id)
    }

    @DgsQuery
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_APPLICATIONS)")
    fun applications(): Iterable<Application> {
        return applicationService.findAll()
    }

    @DgsMutation
    @PreAuthorize(
        "@accessService.isAuthorisedByApplicationId(#id, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).UPDATE_APPLICATION)",
    )
    fun updateApplication(
        id: Int,
        input: UpdateApplicationInput,
    ): Application? {
        return applicationService.update(id, input)
    }

    @DgsMutation
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).UNQUARANTINE_APPLICATIONS)
               || @accessService.isAuthorisedByApplicationId(#id, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).UNQUARANTINE_APPLICATION)""",
    )
    fun unquarantineApplication(
        id: Int,
        input: UnquarantineApplicationInput,
    ): Application? {
        return applicationService.unquarantine(id, input)
    }

    @DgsMutation
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).DELETE_APPLICATION)
               || @accessService.isAuthorisedByApplicationId(#id, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).DELETE_APPLICATION)""",
    )
    fun deleteApplication(id: Int): Boolean {
        return applicationService.deleteById(id)
    }
}