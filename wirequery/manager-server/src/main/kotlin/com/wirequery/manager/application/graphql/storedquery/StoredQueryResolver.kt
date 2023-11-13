package com.wirequery.manager.application.graphql.storedquery

import com.netflix.graphql.dgs.*
import com.wirequery.manager.domain.FunctionalException.Companion.functionalError
import com.wirequery.manager.domain.access.AccessService
import com.wirequery.manager.domain.application.Application
import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum.STORE_QUERY
import com.wirequery.manager.domain.session.Session
import com.wirequery.manager.domain.storedquery.StoredQuery
import com.wirequery.manager.domain.storedquery.StoredQueryService
import com.wirequery.manager.domain.storedquery.StoredQueryService.CreateStoredQueryInput
import com.wirequery.manager.domain.storedquery.StoredQueryService.StoredQueryFilterInput
import org.springframework.security.access.prepost.PreAuthorize
import java.util.concurrent.CompletableFuture

@DgsComponent
@PreAuthorize("isAuthenticated()")
class StoredQueryResolver(
    private val storedQueryService: StoredQueryService,
    private val accessService: AccessService,
) {
    @DgsQuery
    @PreAuthorize(
        """
            hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_STORED_QUERIES)
                || @accessService.isAuthorisedByStoredQueryId(#id, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).VIEW_STORED_QUERY)""",
    )
    fun storedQuery(id: Int): StoredQuery? {
        return storedQueryService.findById(id)
    }

    @DgsQuery
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).VIEW_STORED_QUERIES)")
    fun storedQuerys(filter: StoredQueryFilterInput?): Iterable<StoredQuery> {
        return storedQueryService.findAll(filter)
    }

    @DgsMutation
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).CREATE_STORED_QUERY)")
    fun createStoredQuery(input: CreateStoredQueryInput): StoredQuery {
        if (!accessService.isExpressionAllowed(input.query, STORE_QUERY)) {
            functionalError("Application does not exist or you're not authorized to create a query for it.")
        }
        return storedQueryService.create(input)
    }

    @DgsMutation
    @PreAuthorize(
        """
        hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).DELETE_STORED_QUERY)
            || @accessService.isAuthorisedByStoredQueryId(#id, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).DELETE_STORED_QUERY)""",
    )
    fun deleteStoredQuery(id: Int): Boolean {
        return storedQueryService.deleteById(id)
    }

    @DgsData(parentType = "StoredQuery")
    fun application(dfe: DgsDataFetchingEnvironment): CompletableFuture<Application?> {
        val storedQuery = dfe.getSource<StoredQuery>()
        return dfe.getDataLoader<Int, Application?>("applicationById")
            .load(storedQuery.applicationId)
    }

    @DgsData(parentType = "Application", field = "storedQuerys")
    fun storedQuerysByApplication(dfe: DgsDataFetchingEnvironment): CompletableFuture<Iterable<StoredQuery>> {
        val application = dfe.getSource<Application>()
        val applicationId = application.id
        return dfe.getDataLoader<Int, Iterable<StoredQuery>>("storedQuerysByApplicationId")
            .load(applicationId)
            .thenApply { it ?: listOf() }
    }

    @DgsData(parentType = "Session", field = "storedQuerys")
    fun storedQuerysBySession(dfe: DgsDataFetchingEnvironment): CompletableFuture<Iterable<StoredQuery>> {
        val session = dfe.getSource<Session>()
        val sessionId = session.id
        return dfe.getDataLoader<Int, Iterable<StoredQuery>>("storedQuerysBySessionId")
            .load(sessionId)
            .thenApply { it ?: listOf() }
    }
}
