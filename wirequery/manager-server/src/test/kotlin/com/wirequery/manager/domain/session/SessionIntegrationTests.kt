package com.wirequery.manager.domain.session

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.session.SessionFixtures.CREATE_SESSION_FIXTURE_1
import com.wirequery.manager.domain.template.TemplateFixtures
import com.wirequery.manager.domain.template.TemplateService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SessionIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var templateService: TemplateService

    @Autowired
    private lateinit var sessionService: SessionService

    @Test
    fun `Sessions can be created, updated, fetched and deleted`() {
        val template = templateService.create(TemplateFixtures.CREATE_TEMPLATE_FIXTURE_1)
        val session = sessionService.create(CREATE_SESSION_FIXTURE_1.copy(templateId = template.id), false)

        assertThat(sessionService.findAll()).isNotEmpty
        assertThat(sessionService.findById(session.id)).isNotNull
        assertThat(sessionService.findByIds(listOf(session.id))).isNotEmpty

        sessionService.deleteById(session.id)

        assertThat(sessionService.findAll()).isEmpty()
    }
}
