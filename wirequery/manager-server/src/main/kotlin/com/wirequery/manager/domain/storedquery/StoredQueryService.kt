package com.wirequery.manager.domain.storedquery

import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.FunctionalException.Companion.checkFunctional
import com.wirequery.manager.domain.application.Application
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.query.QueryService
import com.wirequery.manager.domain.storedquery.StoredQueryEvent.StoredQuerysCreatedEvent
import com.wirequery.manager.domain.storedquery.StoredQueryEvent.StoredQuerysDeletedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

@Service
@Transactional
class StoredQueryService(
    private val storedQueryRepository: StoredQueryRepository,
    private val applicationService: ApplicationService,
    private val publisher: ApplicationEventPublisher,
    private val queryService: QueryService,
    private val queryParserService: QueryParserService,
) {
    fun findById(id: Int): StoredQuery? {
        return storedQueryRepository.findByIdOrNull(id)
            ?.let(::toDomainObject)
    }

    fun findByIds(ids: Collection<Int>): List<StoredQuery> {
        if (ids.toList().isEmpty()) {
            return listOf()
        }
        return storedQueryRepository.findByIds(ids)
            .map(::toDomainObject)
    }

    fun findBySessionIds(sessionIds: Collection<Int?>): List<StoredQuery> {
        return storedQueryRepository.findBySessionIds(sessionIds)
            .map(::toDomainObject)
    }

    fun findByApplicationIds(applicationIds: Iterable<Int>): List<StoredQuery> {
        if (applicationIds.toList().isEmpty()) { // TODO test
            return emptyList()
        }
        return storedQueryRepository.findByApplicationIds(applicationIds)
            .map(::toDomainObject)
    }

    fun findAll(filterInput: StoredQueryFilterInput? = null): List<StoredQuery> {
        if (filterInput?.sessionId != null && filterInput.applicationId != null) {
            // TODO error for hasSessionId combined with others
            error("FilterInput only supports a single filter argument.")
        }

        if (filterInput?.sessionId != null) {
            return storedQueryRepository.findBySessionIds(listOf(filterInput.sessionId))
                .map(::toDomainObject)
        }

        if (filterInput?.applicationId != null) {
            return storedQueryRepository.findByApplicationIds(listOf(filterInput.applicationId))
                .map(::toDomainObject)
        }

        if (filterInput?.hasSessionId != null) {
            return if (filterInput.hasSessionId) {
                storedQueryRepository.findByHasSessionId()
            } else {
                storedQueryRepository.findByHasNoSessionId()
            }.map(::toDomainObject)
        }

        return storedQueryRepository.findAll()
            .map(::toDomainObject)
    }

    fun create(input: CreateStoredQueryInput): StoredQuery {
        val appName = queryParserService.parse(input.query).queryHead.appName
        val application =
            applicationService.findByName(appName)
                ?: throw FunctionalException("Application not found: $appName")
        val storedQueryEntity =
            StoredQueryEntity(
                sessionId = input.sessionId,
                applicationId = application.id,
                name = input.name,
                type = input.type,
                query = input.query,
                queryLimit = input.queryLimit,
                endDate = input.endDate?.toInstant()?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) },
                disabled = false,
            )
        val storedQuery = toDomainObject(storedQueryRepository.save(storedQueryEntity))
        queryService.startQuerying(STORED_QUERY_PREFIX + storedQuery.id, storedQuery.query)
        publisher.publishEvent(StoredQuerysCreatedEvent(this, listOf(storedQuery)))
        return storedQuery
    }

    fun deleteById(id: Int): Boolean {
        val storedQuery =
            storedQueryRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return false
        storedQueryRepository.deleteById(id)
        publisher.publishEvent(StoredQuerysDeletedEvent(this, listOf(storedQuery)))
        queryService.stopQueryingByExpression(STORED_QUERY_PREFIX + storedQuery.id, storedQuery.query)
        return true
    }

    fun stopQueryingByApplicationIds(applicationIds: List<Int>): Boolean {
        val storedQueryEntities = storedQueryRepository.findByApplicationIds(applicationIds)
        if (storedQueryEntities.isEmpty()) {
            return false
        }
        storedQueryEntities.forEach {
            storedQueryRepository.save(it.copy(disabled = true))
            queryService.stopQueryingByExpression(STORED_QUERY_PREFIX + it.id, it.query)
        }
        return true
    }

    private fun toDomainObject(entity: StoredQueryEntity) =
        StoredQuery(
            id = entity.id!!,
            sessionId = entity.sessionId,
            applicationId = entity.applicationId,
            name = entity.name,
            type = entity.type,
            query = entity.query,
            queryLimit = entity.queryLimit,
            endDate =
                entity.endDate
                    ?.atZone(ZoneId.systemDefault())
                    ?.toOffsetDateTime(),
            createdAt =
                entity.createdAt!!
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime(),
            updatedAt =
                entity.updatedAt
                    ?.atZone(ZoneId.systemDefault())
                    ?.toOffsetDateTime(),
            createdBy = entity.createdBy,
            updatedBy = entity.updatedBy,
        )

    fun findByName(name: String): StoredQuery? {
        return storedQueryRepository.findByName(name)?.let(::toDomainObject)
    }

    fun findEnabledByApplicationName(appName: String): List<StoredQuery> {
        return applicationService.findByName(appName)?.let {
            storedQueryRepository.findEnabledByApplicationIds(listOf(it.id))
        }?.map(::toDomainObject) ?: listOf()
    }

    fun disableOverdueQueries() {
        val enabled = storedQueryRepository.findEnabledOverdueQueries()
        enabled.forEach {
            queryService.stopQueryingByExpression(STORED_QUERY_PREFIX + it.id, it.query)
            storedQueryRepository.save(it.copy(disabled = true))
        }
    }

    fun disableQueryById(id: Int) {
        val subject = storedQueryRepository.findByIdOrNull(id)
        subject?.let {
            storedQueryRepository.save(it.copy(disabled = true))
            queryService.stopQueryingByExpression(STORED_QUERY_PREFIX + it.id, it.query)
        }
    }

    fun isQuarantined(storedQuery: StoredQuery): Boolean {
        return isQuarantined(storedQuery.applicationId)
    }

    fun isQuarantined(applicationId: Int): Boolean {
        return checkNotNull(applicationService.findById(applicationId)).inQuarantine
    }

    fun restartQuerying(applications: List<Application>) {
        applications
            .map { it.id }
            .let(::findByApplicationIds)
            .forEach { queryService.startQuerying(STORED_QUERY_PREFIX + it.id, it.query) }
    }

    data class CreateStoredQueryInput(
        val sessionId: Int?,
        val name: String,
        val type: String,
        val query: String,
        val queryLimit: Int,
        val endDate: OffsetDateTime?,
    ) {
        init {
            checkFunctional(name.isNotBlank()) { "Name is blank" }
            checkFunctional(query.isNotBlank()) { "Query is blank" }
            checkFunctional(endDate?.isAfter(OffsetDateTime.now()) ?: true) { "End date should be in the future" }
        }
    }

    data class StoredQueryFilterInput(
        val sessionId: Int? = null,
        val applicationId: Int? = null,
        val hasSessionId: Boolean? = null,
    )

    companion object {
        const val STORED_QUERY_PREFIX = "stored_query:"
    }
}
