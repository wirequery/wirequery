// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.templatequery

import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.template.TemplateService
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.CREATE_TEMPLATE_QUERY_FIXTURE_1
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.TEMPLATE_QUERY_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.TEMPLATE_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.UPDATE_TEMPLATE_QUERY_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class TemplateQueryServiceUnitTests {
    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var queryParserService: QueryParserService

    @Mock
    private lateinit var templateQueryRepository: TemplateQueryRepository

    @Mock
    private lateinit var templateService: TemplateService

    @Mock
    private lateinit var applicationService: ApplicationService

    @InjectMocks
    private lateinit var templateQueryService: TemplateQueryService

    @Test
    fun `findById returns the mapped value of findById in TemplateQueryRepository if it is non-empty`() {
        whenever(templateQueryRepository.findById(1))
            .thenReturn(Optional.of(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.findById(1)

        assertThat(actual).isEqualTo(TEMPLATE_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findById returns null if findById in TemplateQueryRepository yields an empty Optional`() {
        whenever(templateQueryRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = templateQueryService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findByIds returns the mapped values of findByIds in TemplateQueryRepository`() {
        whenever(templateQueryRepository.findByIds(listOf(1)))
            .thenReturn(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.findByIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findByTemplateIds returns the contents of the corresponding repository call`() {
        whenever(templateQueryRepository.findByTemplateIds(listOf(1)))
            .thenReturn(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.findByTemplateIds(listOf(1))

        assertThat(actual).isEqualTo(actual)
    }

    @Test
    fun `findByApplicationIds returns the contents of the corresponding repository call`() {
        whenever(templateQueryRepository.findByApplicationIds(listOf(1)))
            .thenReturn(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.findByApplicationIds(listOf(1))

        assertThat(actual).isEqualTo(actual)
    }

    @Test
    fun `findAll returns the values of findAll in TemplateQueryRepository`() {
        whenever(templateQueryRepository.findAll())
            .thenReturn(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.findAll()

        assertThat(actual).containsExactly(TEMPLATE_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findAll returns the values of findAll in CommentRepository when filter is empty`() {
        whenever(templateQueryRepository.findAll())
            .thenReturn(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.findAll(TemplateQueryService.TemplateQueryFilterInput())

        assertThat(actual).containsExactly(TEMPLATE_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findAll delegates to findByTemplateId when filter only contains templateId`() {
        whenever(templateQueryRepository.findByTemplateIds(listOf(1)))
            .thenReturn(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.findAll(TemplateQueryService.TemplateQueryFilterInput(templateId = 1))

        assertThat(actual).containsExactly(TEMPLATE_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findAll delegates to findByApplicationId when filter only contains applicationId`() {
        whenever(templateQueryRepository.findByApplicationIds(listOf(1)))
            .thenReturn(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.findAll(TemplateQueryService.TemplateQueryFilterInput(applicationId = 1))

        assertThat(actual).containsExactly(TEMPLATE_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `create calls save on repository if all requirements are met and publishes an event`() {
        whenever(queryParserService.parse(CREATE_TEMPLATE_QUERY_FIXTURE_1.queryTemplate))
            .thenReturn(
                QueryParserService.Query(
                    queryHead =
                        QueryParserService.QueryHead(
                            appName = "someApp",
                            method = "",
                            path = "",
                            statusCode = "",
                        ),
                    streamOperations = listOf(),
                    aggregatorOperation = null,
                ),
            )

        whenever(templateQueryRepository.save(TEMPLATE_QUERY_ENTITY_FIXTURE_1))
            .thenReturn(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1)

        whenever(templateService.findById(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.templateId))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1)

        whenever(applicationService.findByName("someApp"))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1.copy(id = 10))

        val actual = templateQueryService.create(CREATE_TEMPLATE_QUERY_FIXTURE_1)

        assertThat(actual).isEqualTo(TEMPLATE_QUERY_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(
                TemplateQueryEvent.TemplateQuerysCreatedEvent(
                    templateQueryService,
                    listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1),
                ),
            )
    }

    @Test
    fun `create fails when related objects cannot be found and publishes no events`() {
        assertThrows<RuntimeException> {
            templateQueryService.create(CREATE_TEMPLATE_QUERY_FIXTURE_1)
        }

        verify(templateQueryRepository, times(0)).save(TEMPLATE_QUERY_ENTITY_FIXTURE_1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `update calls save on repository if all requirements are met and publishes an event`() {
        whenever(queryParserService.parse(UPDATE_TEMPLATE_QUERY_FIXTURE_1.queryTemplate))
            .thenReturn(
                QueryParserService.Query(
                    queryHead =
                        QueryParserService.QueryHead(
                            appName = "someApp",
                            method = "",
                            path = "",
                            statusCode = "",
                        ),
                    streamOperations = listOf(),
                    aggregatorOperation = null,
                ),
            )

        whenever(templateQueryRepository.findById(1))
            .thenReturn(Optional.of(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        whenever(templateQueryRepository.save(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))
            .thenReturn(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1)

        whenever(templateService.findById(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1.templateId))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1)

        whenever(applicationService.findByName("someApp"))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1.copy(id = 10))

        val actual = templateQueryService.update(1, UPDATE_TEMPLATE_QUERY_FIXTURE_1)

        assertThat(actual).isEqualTo(TEMPLATE_QUERY_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(
                TemplateQueryEvent.TemplateQuerysUpdatedEvent(
                    templateQueryService,
                    listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1),
                ),
            )
    }

    @Test
    fun `update fails when related objects cannot be found and publishes no events`() {
        assertThrows<RuntimeException> {
            templateQueryService.update(1, UPDATE_TEMPLATE_QUERY_FIXTURE_1)
        }

        verify(templateQueryRepository, times(0)).save(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `deleteById deletes the TemplateQuery identified by id in the repository if it exists and publishes an event`() {
        whenever(templateQueryRepository.findById(1))
            .thenReturn(Optional.of(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        templateQueryService.deleteById(1)

        verify(templateQueryRepository).deleteById(1)

        verify(publisher)
            .publishEvent(
                TemplateQueryEvent.TemplateQuerysDeletedEvent(
                    templateQueryService,
                    listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1),
                ),
            )
    }

    @Test
    fun `deleteById does not delete the TemplateQuery identified by id in the repository if it doesn't exist and publishes no events`() {
        whenever(templateQueryRepository.findById(1))
            .thenReturn(Optional.empty())

        templateQueryService.deleteById(1)

        verify(templateQueryRepository, times(0)).deleteById(1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `deleteByTemplateId deletes templates by templateId and publishes event`() {
        whenever(templateQueryRepository.findByTemplateIds(listOf(10)))
            .thenReturn(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.deleteByTemplateIds(listOf(10))
        assertThat(actual).isEqualTo(true)

        verify(templateQueryRepository).deleteAll(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))
        verify(publisher).publishEvent(
            TemplateQueryEvent.TemplateQuerysDeletedEvent(
                templateQueryService,
                listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1),
            ),
        )
    }

    @Test
    fun `deleteByTemplateId returns false if no templates are deleted and publishes no events`() {
        whenever(templateQueryRepository.findByTemplateIds(listOf(10)))
            .thenReturn(listOf())

        val actual = templateQueryService.deleteByTemplateIds(listOf(10))
        assertThat(actual).isEqualTo(false)

        verify(templateQueryRepository, times(0)).deleteAll(any())
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `deleteByApplicationId deletes applications by applicationId and publishes event`() {
        whenever(templateQueryRepository.findByApplicationIds(listOf(10)))
            .thenReturn(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = templateQueryService.deleteByApplicationIds(listOf(10))
        assertThat(actual).isEqualTo(true)

        verify(templateQueryRepository).deleteAll(listOf(TEMPLATE_QUERY_ENTITY_FIXTURE_WITH_ID_1))
        verify(publisher).publishEvent(
            TemplateQueryEvent.TemplateQuerysDeletedEvent(
                templateQueryService,
                listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1),
            ),
        )
    }

    @Test
    fun `deleteByApplicationId returns false if no applications are deleted and publishes no events`() {
        whenever(templateQueryRepository.findByApplicationIds(listOf(10)))
            .thenReturn(listOf())

        val actual = templateQueryService.deleteByApplicationIds(listOf(10))
        assertThat(actual).isEqualTo(false)

        verify(templateQueryRepository, times(0)).deleteAll(any())
        verify(publisher, times(0)).publishEvent(any())
    }
}
