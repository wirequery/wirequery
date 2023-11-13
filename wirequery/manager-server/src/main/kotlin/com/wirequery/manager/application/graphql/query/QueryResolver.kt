package com.wirequery.manager.application.graphql.query

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsSubscription
import com.wirequery.manager.domain.query.QueryReport
import com.wirequery.manager.domain.query.QueryService
import org.reactivestreams.Publisher
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Flux
import java.time.Duration

@DgsComponent
@PreAuthorize("isAuthenticated()")
class QueryResolver(private val queryService: QueryService) {
    @DgsSubscription
    @PreAuthorize(
        """hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).QUERY.name())
            && @accessService.isExpressionAllowed(#expression, T(com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum).QUERY)""",
    )
    fun query(expression: String): Publisher<QueryReportOrHeartbeat> {
        val queryFlux = Flux.create { queryService.query(expression, it) }
        return Flux.merge(
            Flux
                .interval(Duration.ofSeconds(30))
                .map { QueryReportOrHeartbeat(null) },
            queryFlux.map { QueryReportOrHeartbeat(it) },
        )
    }

    data class QueryReportOrHeartbeat(
        val queryReport: QueryReport?,
    )
}
