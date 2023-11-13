package com.wirequery.manager.domain.application

import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.FunctionalException.Companion.checkFunctional
import com.wirequery.manager.domain.application.ApplicationEvent.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.relational.core.conversion.DbActionExecutionException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId

@Service
@Transactional
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val publisher: ApplicationEventPublisher,
    private val apiKeyGeneratorService: ApiKeyGeneratorService,
) {
    fun findById(id: Int): Application? {
        return applicationRepository.findByIdOrNull(id)
            ?.let(::toDomainObject)
    }

    fun findApiKeyById(id: Int): String? {
        val application =
            applicationRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return null

        publisher.publishEvent(ApplicationsApiKeyRequestedEvent(this, listOf(application)))
        return application.apiKey
    }

    fun findByIds(ids: Iterable<Int>): List<Application> {
        return applicationRepository.findByIds(ids)
            .map(::toDomainObject)
    }

    fun findAll(): List<Application> {
        return applicationRepository.findAll()
            .map(::toDomainObject)
    }

    fun create(input: CreateApplicationInput): Application {
        try {
            val applicationEntity =
                ApplicationEntity(
                    name = input.name,
                    description = input.description,
                    apiKey = apiKeyGeneratorService.generateApiKey(),
                    inQuarantine = false,
                    quarantineRule = null,
                    quarantineReason = null,
                )
            val application = toDomainObject(applicationRepository.save(applicationEntity))
            publisher.publishEvent(ApplicationsCreatedEvent(this, listOf(application)))
            return application
        } catch (e: DbActionExecutionException) {
            if (e.cause is DuplicateKeyException) {
                throw FunctionalException(
                    "An application with the name ${input.name} already exists.",
                    e,
                )
            }
            throw e
        }
    }

    fun update(
        id: Int,
        input: UpdateApplicationInput,
    ): Application? {
        val applicationEntity = applicationRepository.findByIdOrNull(id) ?: return null
        val application =
            applicationRepository.save(
                applicationEntity.copy(
                    description = input.description ?: applicationEntity.description,
                ),
            ).let(::toDomainObject)
        publisher.publishEvent(ApplicationsUpdatedEvent(this, listOf(application)))
        return application
    }

    fun quarantine(
        applicationName: String,
        quarantineRule: String,
        quarantineReason: String,
    ) {
        val entity =
            applicationRepository.findByName(applicationName)
                ?.copy(inQuarantine = true, quarantineRule = quarantineRule, quarantineReason = quarantineReason)
                ?: return

        applicationRepository.save(entity)

        publisher.publishEvent(
            ApplicationsQuarantinedEvent(
                _source = this,
                quarantineRule = quarantineRule,
                quarantineReason = quarantineReason,
                entities = listOf(toDomainObject(entity)),
            ),
        )
    }

    fun unquarantine(
        id: Int,
        unquarantineApplicationInput: UnquarantineApplicationInput,
    ): Application? {
        val entity =
            applicationRepository.findByIdOrNull(id)
                ?.copy(inQuarantine = false, quarantineRule = null, quarantineReason = null)
                ?: return null

        applicationRepository.save(entity)

        val domainObject = toDomainObject(entity)

        publisher.publishEvent(
            ApplicationsUnquarantinedEvent(
                _source = this,
                unquarantineReason = unquarantineApplicationInput.reason,
                entities = listOf(domainObject),
            ),
        )

        return domainObject
    }

    fun deleteById(id: Int): Boolean {
        val application =
            applicationRepository.findByIdOrNull(id)
                ?.let(::toDomainObject)
                ?: return false
        publisher.publishEvent(BeforeApplicationsDeletedEvent(this, listOf(application)))
        applicationRepository.deleteById(id)
        publisher.publishEvent(ApplicationsDeletedEvent(this, listOf(application)))
        return true
    }

    fun isApiKeyValid(
        applicationName: String,
        apiKey: String,
    ): Boolean {
        return applicationRepository.findByName(applicationName)?.apiKey == apiKey
    }

    fun isQuarantined(appName: String): Boolean {
        return applicationRepository.findByName(appName)?.inQuarantine ?: false
    }

    fun findByName(appName: String): Application? {
        return applicationRepository.findByName(appName)?.let(::toDomainObject)
    }

    private fun toDomainObject(entity: ApplicationEntity) =
        Application(
            id = entity.id!!,
            name = entity.name,
            description = entity.description,
            apiKey = entity.apiKey,
            inQuarantine = entity.inQuarantine,
            quarantineRule = entity.quarantineRule,
            quarantineReason = entity.quarantineReason,
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

    data class CreateApplicationInput(
        val name: String,
        val description: String,
    ) {
        init {
            checkFunctional(name.isNotBlank()) { "Name is blank" }
            checkFunctional(!name.contains(" ")) { "Name contains spaces" }
        }
    }

    data class UpdateApplicationInput(
        val description: String?,
    )

    data class UnquarantineApplicationInput(
        val reason: String,
    )
}
