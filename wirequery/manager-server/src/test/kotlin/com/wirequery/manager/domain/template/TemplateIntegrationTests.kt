package com.wirequery.manager.domain.template

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.template.TemplateFixtures.CREATE_TEMPLATE_FIXTURE_1
import com.wirequery.manager.domain.template.TemplateFixtures.UPDATE_TEMPLATE_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TemplateIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var templateService: TemplateService

    @Test
    fun `Templates can be created, updated, fetched and deleted`() {
        var template = templateService.create(CREATE_TEMPLATE_FIXTURE_1)
        template = templateService.update(template.id, UPDATE_TEMPLATE_FIXTURE_1)!!

        assertThat(templateService.findAll()).isNotEmpty
        assertThat(templateService.findById(template.id)).isNotNull
        assertThat(templateService.findByIds(listOf(template.id))).isNotEmpty

        templateService.deleteById(template.id)

        assertThat(templateService.findAll()).isEmpty()
    }
}
