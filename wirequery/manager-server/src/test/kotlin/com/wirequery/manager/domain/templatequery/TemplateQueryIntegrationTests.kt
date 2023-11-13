package com.wirequery.manager.domain.templatequery

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationRepository
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.template.TemplateRepository
import com.wirequery.manager.domain.template.TemplateService
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.CREATE_TEMPLATE_QUERY_FIXTURE_1
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.UPDATE_TEMPLATE_QUERY_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class TemplateQueryIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var templateQueryService: TemplateQueryService

    @Autowired
    private lateinit var templateService: TemplateService

    @Autowired
    private lateinit var templateRepository: TemplateRepository

    @Autowired
    private lateinit var applicationService: ApplicationService

    @Autowired
    private lateinit var applicationRepository: ApplicationRepository

    @Test
    fun `TemplateQuerys cannot be created if related entities do not exist`() {
        assertThrows<RuntimeException> {
            templateQueryService.create(CREATE_TEMPLATE_QUERY_FIXTURE_1)
        }
        assertThat(templateQueryService.findAll()).isEmpty()
    }

    @Test
    fun `TemplateQuerys can be created, updated, fetched and deleted`() {
        val template = templateRepository.save(TEMPLATE_ENTITY_FIXTURE_1)

        val application = applicationRepository.save(APPLICATION_ENTITY_FIXTURE_1)

        var templateQuery =
            templateQueryService.create(
                CREATE_TEMPLATE_QUERY_FIXTURE_1.copy(
                    templateId = template.id!!,
                    queryTemplate = application.name,
                ),
            )
        templateQuery =
            templateQueryService.update(
                templateQuery.id,
                UPDATE_TEMPLATE_QUERY_FIXTURE_1.copy(
                    templateId = template.id,
                    queryTemplate = application.name,
                ),
            )!!

        assertThat(templateQueryService.findAll()).isNotEmpty
        assertThat(templateQueryService.findById(templateQuery.id)).isNotNull
        assertThat(templateQueryService.findByIds(listOf(templateQuery.id))).isNotEmpty

        assertThat(templateQueryService.findAll(TemplateQueryService.TemplateQueryFilterInput(templateId = template.id))).isNotEmpty
        assertThat(templateQueryService.findByTemplateIds(listOf(template.id!!))).isNotEmpty

        assertThat(templateQueryService.findAll(TemplateQueryService.TemplateQueryFilterInput(applicationId = application.id!!))).isNotEmpty
        assertThat(templateQueryService.findByApplicationIds(listOf(application.id!!))).isNotEmpty

        templateQueryService.deleteById(templateQuery.id)

        assertThat(templateQueryService.findAll()).isEmpty()
    }

    @Test
    fun `TemplateQuerys are deleted when related Templates are deleted`() {
        val template = templateRepository.save(TEMPLATE_ENTITY_FIXTURE_1)
        val application = applicationRepository.save(APPLICATION_ENTITY_FIXTURE_1)

        val templateQuery =
            templateQueryService.create(
                CREATE_TEMPLATE_QUERY_FIXTURE_1.copy(
                    templateId = template.id!!,
                    queryTemplate = application.name,
                ),
            )

        templateService.deleteById(templateQuery.templateId)

        assertThat(templateQueryService.findAll()).isEmpty()
    }

    @Test
    fun `TemplateQuerys are deleted when related Applications are deleted`() {
        val template = templateRepository.save(TEMPLATE_ENTITY_FIXTURE_1)
        val application = applicationRepository.save(APPLICATION_ENTITY_FIXTURE_1)

        val templateQuery =
            templateQueryService.create(
                CREATE_TEMPLATE_QUERY_FIXTURE_1.copy(
                    templateId = template.id!!,
                    queryTemplate = application.name,
                ),
            )

        applicationService.deleteById(templateQuery.applicationId)

        assertThat(templateQueryService.findAll()).isEmpty()
    }
}
