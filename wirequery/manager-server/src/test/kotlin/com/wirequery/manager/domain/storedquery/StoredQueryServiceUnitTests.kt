// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.storedquery

import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.query.QueryParserService
import com.wirequery.manager.domain.query.QueryService
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.CREATE_STORED_QUERY_FIXTURE_1
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.STORED_QUERY_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.STORED_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryService.Companion.STORED_QUERY_PREFIX
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import java.time.*
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class StoredQueryServiceUnitTests {
    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var storedQueryRepository: StoredQueryRepository

    @Mock
    private lateinit var applicationService: ApplicationService

    @Mock
    private lateinit var queryService: QueryService

    @Mock
    private lateinit var queryParserService: QueryParserService

    @InjectMocks
    private lateinit var storedQueryService: StoredQueryService

    @Test
    fun `findById returns the mapped value of findById in StoredQueryRepository if it is non-empty`() {
        whenever(storedQueryRepository.findById(1))
            .thenReturn(Optional.of(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.findById(1)

        assertThat(actual).isEqualTo(STORED_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findById returns null if findById in StoredQueryRepository yields an empty Optional`() {
        whenever(storedQueryRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = storedQueryService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findByIds does not call repository when ids is empty`() {
        val actual = storedQueryService.findByIds(listOf())
        assertThat(actual).isEqualTo(listOf<StoredQuery>())
        verify(storedQueryRepository, times(0)).findByIds(listOf(1))
    }

    @Test
    fun `findByIds returns the mapped values of findByIds in StoredQueryRepository`() {
        whenever(storedQueryRepository.findByIds(listOf(1)))
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.findByIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(STORED_QUERY_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findByApplicationIds returns the contents of the corresponding repository call`() {
        whenever(storedQueryRepository.findByApplicationIds(listOf(1)))
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.findByApplicationIds(listOf(1))

        assertThat(actual).isEqualTo(actual)
    }

    @Test
    fun `findAll returns the values of findAll in StoredQueryRepository`() {
        whenever(storedQueryRepository.findAll())
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.findAll()

        assertThat(actual).containsExactly(STORED_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findAll returns the values of findAll in CommentRepository when filter is empty`() {
        whenever(storedQueryRepository.findAll())
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.findAll(StoredQueryService.StoredQueryFilterInput())

        assertThat(actual).containsExactly(STORED_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findAll delegates to findByApplicationId when filter only contains applicationId`() {
        whenever(storedQueryRepository.findByApplicationIds(listOf(1)))
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.findAll(StoredQueryService.StoredQueryFilterInput(applicationId = 1))

        assertThat(actual).containsExactly(STORED_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findAll delegates to findByHasSessionId when filter contains applicationId`() {
        whenever(storedQueryRepository.findByHasSessionId())
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.findAll(StoredQueryService.StoredQueryFilterInput(hasSessionId = true))

        assertThat(actual).containsExactly(STORED_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findAll delegates to findByHasNoSessionId when filter contains applicationId`() {
        whenever(storedQueryRepository.findByHasNoSessionId())
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.findAll(StoredQueryService.StoredQueryFilterInput(hasSessionId = false))

        assertThat(actual).containsExactly(STORED_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `create calls save on repository if all requirements are met and publishes an event`() {
        whenever(queryParserService.parse(STORED_QUERY_ENTITY_FIXTURE_1.query))
            .thenCallRealMethod()

        whenever(applicationService.findByName(APPLICATION_FIXTURE_WITH_ID_1.name))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1.copy(id = STORED_QUERY_FIXTURE_WITH_ID_1.applicationId))

        whenever(storedQueryRepository.save(STORED_QUERY_ENTITY_FIXTURE_1))
            .thenReturn(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1)

        val actual = storedQueryService.create(CREATE_STORED_QUERY_FIXTURE_1)

        assertThat(actual).isEqualTo(STORED_QUERY_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(
                StoredQueryEvent.StoredQuerysCreatedEvent(
                    storedQueryService,
                    listOf(STORED_QUERY_FIXTURE_WITH_ID_1),
                ),
            )
    }

    @Test
    fun `create fails when related objects cannot be found and publishes no events`() {
        assertThrows<RuntimeException> {
            storedQueryService.create(CREATE_STORED_QUERY_FIXTURE_1)
        }

        verify(storedQueryRepository, times(0)).save(STORED_QUERY_ENTITY_FIXTURE_1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `deleteById deletes the StoredQuery identified by id in the repository if it exists and publishes an event`() {
        whenever(storedQueryRepository.findById(1))
            .thenReturn(Optional.of(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        storedQueryService.deleteById(1)

        verify(storedQueryRepository).deleteById(1)

        verify(publisher)
            .publishEvent(
                StoredQueryEvent.StoredQuerysDeletedEvent(
                    storedQueryService,
                    listOf(STORED_QUERY_FIXTURE_WITH_ID_1),
                ),
            )
    }

    @Test
    fun `deleteById does not delete the StoredQuery identified by id in the repository if it doesn't exist and publishes no events`() {
        whenever(storedQueryRepository.findById(1))
            .thenReturn(Optional.empty())

        storedQueryService.deleteById(1)

        verify(storedQueryRepository, times(0)).deleteById(1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `stopQueryingByApplicationIds updates disabled, deletes applications by applicationId`() {
        whenever(storedQueryRepository.findByApplicationIds(listOf(10)))
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.stopQueryingByApplicationIds(listOf(10))
        assertThat(actual).isEqualTo(true)

        verify(queryService).stopQueryingByExpression(
            STORED_QUERY_PREFIX + STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.id,
            STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.query,
        )

        verify(storedQueryRepository)
            .save(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.copy(disabled = true))
    }

    @Test
    fun `deleteByApplicationId returns false if no applications are deleted and publishes no events`() {
        whenever(storedQueryRepository.findByApplicationIds(listOf(10)))
            .thenReturn(listOf())

        val actual = storedQueryService.stopQueryingByApplicationIds(listOf(10))
        assertThat(actual).isEqualTo(false)

        verify(storedQueryRepository, times(0)).deleteAll(any())
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `findByName calls findByName on the repository`() {
        whenever(storedQueryRepository.findByName(STORED_QUERY_ENTITY_FIXTURE_1.name))
            .thenReturn(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1)

        val actual = storedQueryService.findByName(STORED_QUERY_ENTITY_FIXTURE_1.name)
        assertThat(actual).isEqualTo(STORED_QUERY_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findByApplicationName calls findByName on the repository`() {
        whenever(applicationService.findByName(APPLICATION_ENTITY_FIXTURE_1.name))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1)

        whenever(storedQueryRepository.findEnabledByApplicationIds(listOf(APPLICATION_FIXTURE_WITH_ID_1.id)))
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        val actual = storedQueryService.findEnabledByApplicationName(APPLICATION_ENTITY_FIXTURE_1.name)
        assertThat(actual).isEqualTo(listOf(STORED_QUERY_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `disableOverdueQueries disables all queries that are overdue`() {
        whenever(storedQueryRepository.findEnabledOverdueQueries())
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        storedQueryService.disableOverdueQueries()

        verify(queryService)
            .stopQueryingByExpression(
                queryId = STORED_QUERY_PREFIX + STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.id,
                expression = STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.query,
            )

        verify(storedQueryRepository).save(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.copy(disabled = true))
    }

    @Test
    fun `disableQuery disables query by id`() {
        whenever(storedQueryRepository.findById(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.id!!))
            .thenReturn(Optional.of(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        storedQueryService.disableQueryById(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.id!!)

        verify(storedQueryRepository)
            .save(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.copy(disabled = true))

        verify(queryService)
            .stopQueryingByExpression(
                queryId = STORED_QUERY_PREFIX + STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.id,
                expression = STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.query,
            )
    }

    @Test
    fun `isQuarantined throws an Exception if the related application does not exist`() {
        whenever(applicationService.findById(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId))
            .thenReturn(null)

        assertThrows<IllegalStateException> {
            storedQueryService.isQuarantined(STORED_QUERY_FIXTURE_WITH_ID_1)
        }
    }

    @Test
    fun `isQuarantined checks if a stored query is quarantined`() {
        whenever(applicationService.findById(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1)

        val actual = storedQueryService.isQuarantined(STORED_QUERY_FIXTURE_WITH_ID_1)

        assertThat(actual).isEqualTo(APPLICATION_FIXTURE_WITH_ID_1.inQuarantine)
    }

    @Test
    fun `restartQuerying restarts all stored queries by their respective applications`() {
        whenever(storedQueryRepository.findByApplicationIds(listOf(APPLICATION_FIXTURE_WITH_ID_1.id)))
            .thenReturn(listOf(STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1))

        storedQueryService.restartQuerying(listOf(APPLICATION_FIXTURE_WITH_ID_1))

        verify(queryService)
            .startQuerying(
                STORED_QUERY_PREFIX + STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.id,
                STORED_QUERY_ENTITY_FIXTURE_WITH_ID_1.query,
            )
    }

    @Nested
    inner class CreateStoredQueryInputTest {
        @Test
        fun `name should not be blank`() {
            val exception =
                assertThrows<FunctionalException> {
                    CREATE_STORED_QUERY_FIXTURE_1.copy(name = " ")
                }
            assertThat(exception.message).isEqualTo("Name is blank")
        }

        @Test
        fun `query should not be blank`() {
            val exception =
                assertThrows<FunctionalException> {
                    CREATE_STORED_QUERY_FIXTURE_1.copy(query = " ")
                }
            assertThat(exception.message).isEqualTo("Query is blank")
        }

        @Test
        fun `endDate can be null`() {
            assertDoesNotThrow {
                CREATE_STORED_QUERY_FIXTURE_1.copy(endDate = null)
            }
        }

        @Test
        fun `endDate should be in the future`() {
            assertDoesNotThrow {
                CREATE_STORED_QUERY_FIXTURE_1.copy(endDate = OffsetDateTime.now().plusDays(1))
            }
        }

        @Test
        fun `endDate should not be in the past`() {
            val exception =
                assertThrows<FunctionalException> {
                    CREATE_STORED_QUERY_FIXTURE_1.copy(endDate = OffsetDateTime.now().minusDays(1))
                }
            assertThat(exception.message).isEqualTo("End date should be in the future")
        }
    }
}
