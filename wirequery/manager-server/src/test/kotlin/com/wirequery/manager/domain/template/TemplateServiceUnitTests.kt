// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.template

import com.wirequery.manager.domain.application.ApiKeyGeneratorService
import com.wirequery.manager.domain.template.TemplateFixtures.CREATE_TEMPLATE_FIXTURE_1
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.template.TemplateFixtures.UPDATE_TEMPLATE_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class TemplateServiceUnitTests {
    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var templateRepository: TemplateRepository

    @Mock
    private lateinit var apiKeyGeneratorService: ApiKeyGeneratorService

    @InjectMocks
    private lateinit var templateService: TemplateService

    @Test
    fun `findById returns the mapped value of findById in TemplateRepository if it is non-empty`() {
        whenever(templateRepository.findById(1))
            .thenReturn(Optional.of(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateService.findById(1)

        assertThat(actual).isEqualTo(TEMPLATE_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findById returns null if findById in TemplateRepository yields an empty Optional`() {
        whenever(templateRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = templateService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findByIds returns the mapped values of findByIds in TemplateRepository`() {
        whenever(templateRepository.findByIds(listOf(1)))
            .thenReturn(listOf(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateService.findByIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(TEMPLATE_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findAll returns the values of findAll in TemplateRepository`() {
        whenever(templateRepository.findAll())
            .thenReturn(listOf(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateService.findAll()

        assertThat(actual).containsExactly(TEMPLATE_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `create calls save on repository if all requirements are met and publishes an event`() {
        whenever(apiKeyGeneratorService.generateApiKey())
            .thenReturn(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1.apiKey)

        whenever(templateRepository.save(TEMPLATE_ENTITY_FIXTURE_1))
            .thenReturn(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1)

        val actual = templateService.create(CREATE_TEMPLATE_FIXTURE_1)

        assertThat(actual).isEqualTo(TEMPLATE_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(TemplateEvent.TemplatesCreatedEvent(templateService, listOf(TEMPLATE_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `update calls save on repository if all requirements are met and publishes an event`() {
        whenever(templateRepository.findById(1))
            .thenReturn(Optional.of(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1))

        whenever(templateRepository.save(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1))
            .thenReturn(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1)

        val actual = templateService.update(1, UPDATE_TEMPLATE_FIXTURE_1)

        assertThat(actual).isEqualTo(TEMPLATE_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(TemplateEvent.TemplatesUpdatedEvent(templateService, listOf(TEMPLATE_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `deleteById deletes the Template identified by id in the repository if it exists and publishes an event`() {
        whenever(templateRepository.findById(1))
            .thenReturn(Optional.of(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1))

        templateService.deleteById(1)

        verify(templateRepository).deleteById(1)

        verify(publisher)
            .publishEvent(TemplateEvent.TemplatesDeletedEvent(templateService, listOf(TEMPLATE_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `deleteById does not delete the Template identified by id in the repository if it doesn't exist and publishes no events`() {
        whenever(templateRepository.findById(1))
            .thenReturn(Optional.empty())

        templateService.deleteById(1)

        verify(templateRepository, times(0)).deleteById(1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @CsvSource(
        "123-456, 123-456, true",
        "123-456, 234-567, false"
    )
    @ParameterizedTest
    fun `verifyApiKey returns whether the api keys match`(argApiKey: String, dbApiKey: String, expected: Boolean) {
        whenever(templateRepository.findById(TEMPLATE_FIXTURE_WITH_ID_1.id))
            .thenReturn(Optional.of(TEMPLATE_ENTITY_FIXTURE_WITH_ID_1.copy(apiKey = dbApiKey)))

        assertThat(templateService.verifyApiKey(TEMPLATE_FIXTURE_WITH_ID_1.id, argApiKey))
            .isEqualTo(expected)
    }
}
