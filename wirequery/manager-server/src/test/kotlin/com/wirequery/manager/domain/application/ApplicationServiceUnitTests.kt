// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.application

import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.application.ApplicationEvent.ApplicationsApiKeyRequestedEvent
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.application.ApplicationFixtures.CREATE_APPLICATION_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationFixtures.UPDATE_APPLICATION_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationService.UnquarantineApplicationInput
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.relational.core.conversion.DbActionExecutionException
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class ApplicationServiceUnitTests {
    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var applicationRepository: ApplicationRepository

    @Mock
    private lateinit var apiKeyGeneratorService: ApiKeyGeneratorService

    @InjectMocks
    private lateinit var applicationService: ApplicationService

    @Test
    fun `findById returns the contained value of findById in ApplicationRepository if it is non-empty`() {
        whenever(applicationRepository.findById(1))
            .thenReturn(Optional.of(APPLICATION_ENTITY_FIXTURE_WITH_ID_1))

        val actual = applicationService.findById(1)

        assertThat(actual).isEqualTo(APPLICATION_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findById returns null if findById in ApplicationRepository yields an empty Optional`() {
        whenever(applicationRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = applicationService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findApiKeyById returns the api key value of findById in ApplicationRepository and publishes event if it is non-empty`() {
        whenever(applicationRepository.findById(1))
            .thenReturn(Optional.of(APPLICATION_ENTITY_FIXTURE_WITH_ID_1))

        val actual = applicationService.findApiKeyById(1)

        assertThat(actual).isEqualTo(APPLICATION_FIXTURE_WITH_ID_1.apiKey)

        verify(publisher)
            .publishEvent(ApplicationsApiKeyRequestedEvent(applicationService, listOf(APPLICATION_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `findApiKeyById returns null if findById in ApplicationRepository yields an empty Optional`() {
        whenever(applicationRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = applicationService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findByIds returns the contained values of findByIds in ApplicationRepository`() {
        whenever(applicationRepository.findByIds(listOf(1)))
            .thenReturn(listOf(APPLICATION_ENTITY_FIXTURE_WITH_ID_1))

        val actual = applicationService.findByIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(APPLICATION_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findAll returns the values of findAll in ApplicationRepository`() {
        whenever(applicationRepository.findAll())
            .thenReturn(listOf(APPLICATION_ENTITY_FIXTURE_WITH_ID_1))

        val actual = applicationService.findAll()

        assertThat(actual).containsExactly(APPLICATION_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `create calls save on repository if all requirements are met and publishes an event`() {
        whenever(apiKeyGeneratorService.generateApiKey())
            .thenReturn(APPLICATION_ENTITY_FIXTURE_1.apiKey)

        whenever(applicationRepository.save(APPLICATION_ENTITY_FIXTURE_1))
            .thenReturn(APPLICATION_ENTITY_FIXTURE_WITH_ID_1)

        val actual = applicationService.create(CREATE_APPLICATION_FIXTURE_1)

        assertThat(actual).isEqualTo(APPLICATION_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(
                ApplicationEvent.ApplicationsCreatedEvent(
                    applicationService,
                    listOf(APPLICATION_FIXTURE_WITH_ID_1),
                ),
            )
    }

    @Test
    fun `create catches duplicate errors and throws the corresponding functional exception`() {
        whenever(apiKeyGeneratorService.generateApiKey())
            .thenReturn(APPLICATION_ENTITY_FIXTURE_1.apiKey)

        doThrow(DbActionExecutionException(mock(), DuplicateKeyException("")))
            .whenever(applicationRepository)
            .save(APPLICATION_ENTITY_FIXTURE_1)

        val actual =
            assertThrows<FunctionalException> {
                applicationService.create(CREATE_APPLICATION_FIXTURE_1)
            }

        assertThat(actual.message).isEqualTo("An application with the name ${APPLICATION_ENTITY_FIXTURE_1.name} already exists.")

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `update calls save on repository if all requirements are met and publishes an event`() {
        whenever(applicationRepository.findById(1))
            .thenReturn(Optional.of(APPLICATION_ENTITY_FIXTURE_WITH_ID_1))

        whenever(applicationRepository.save(APPLICATION_ENTITY_FIXTURE_WITH_ID_1))
            .thenReturn(APPLICATION_ENTITY_FIXTURE_WITH_ID_1)

        val actual = applicationService.update(1, UPDATE_APPLICATION_FIXTURE_1)

        assertThat(actual).isEqualTo(APPLICATION_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(
                ApplicationEvent.ApplicationsUpdatedEvent(
                    applicationService,
                    listOf(APPLICATION_FIXTURE_WITH_ID_1),
                ),
            )
    }

    @Test
    fun `quarantine sets the quarantine flags`() {
        whenever(applicationRepository.findByName(APPLICATION_FIXTURE_WITH_ID_1.name))
            .thenReturn(APPLICATION_ENTITY_FIXTURE_WITH_ID_1)

        applicationService.quarantine(APPLICATION_FIXTURE_WITH_ID_1.name, "some rule", "some reason")

        val expected =
            APPLICATION_FIXTURE_WITH_ID_1.copy(
                inQuarantine = true,
                quarantineRule = "some rule",
                quarantineReason = "some reason",
            )

        verify(applicationRepository).save(
            APPLICATION_ENTITY_FIXTURE_WITH_ID_1.copy(
                inQuarantine = true,
                quarantineRule = "some rule",
                quarantineReason = "some reason",
            ),
        )

        verify(publisher).publishEvent(
            ApplicationEvent.ApplicationsQuarantinedEvent(
                _source = applicationService,
                quarantineRule = "some rule",
                quarantineReason = "some reason",
                entities = listOf(expected),
            ),
        )
    }

    @Test
    fun `unquarantine resets the quarantine flags`() {
        whenever(applicationRepository.findById(APPLICATION_FIXTURE_WITH_ID_1.id))
            .thenReturn(Optional.of(APPLICATION_ENTITY_FIXTURE_WITH_ID_1))

        val savedObject =
            applicationService.unquarantine(
                APPLICATION_FIXTURE_WITH_ID_1.id,
                UnquarantineApplicationInput("problem solved"),
            )

        val expected =
            APPLICATION_FIXTURE_WITH_ID_1.copy(
                inQuarantine = false,
                quarantineRule = null,
                quarantineReason = null,
            )

        assertThat(savedObject).isEqualTo(expected)

        verify(applicationRepository).save(
            APPLICATION_ENTITY_FIXTURE_WITH_ID_1.copy(
                inQuarantine = false,
                quarantineRule = null,
                quarantineReason = null,
            ),
        )

        verify(publisher).publishEvent(
            ApplicationEvent.ApplicationsUnquarantinedEvent(
                _source = applicationService,
                unquarantineReason = "problem solved",
                entities = listOf(expected),
            ),
        )
    }

    @Test
    fun `deleteById deletes the Application identified by id in the repository if it exists and publishes events`() {
        whenever(applicationRepository.findById(1))
            .thenReturn(Optional.of(APPLICATION_ENTITY_FIXTURE_WITH_ID_1))

        applicationService.deleteById(1)

        verify(publisher)
            .publishEvent(
                ApplicationEvent.BeforeApplicationsDeletedEvent(
                    applicationService,
                    listOf(APPLICATION_FIXTURE_WITH_ID_1),
                ),
            )

        verify(applicationRepository).deleteById(1)

        verify(publisher)
            .publishEvent(
                ApplicationEvent.ApplicationsDeletedEvent(
                    applicationService,
                    listOf(APPLICATION_FIXTURE_WITH_ID_1),
                ),
            )
    }

    @Test
    fun `deleteById does not delete the Application identified by id in the repository if it doesn't exist and publishes no events`() {
        whenever(applicationRepository.findById(1))
            .thenReturn(Optional.empty())

        applicationService.deleteById(1)

        verify(applicationRepository, times(0)).deleteById(1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `isApiKeyValid returns true if api key matches api key`() {
        whenever(applicationRepository.findByName(APPLICATION_ENTITY_FIXTURE_1.name))
            .thenReturn(APPLICATION_ENTITY_FIXTURE_1)

        assertThat(
            applicationService.isApiKeyValid(
                APPLICATION_ENTITY_FIXTURE_1.name,
                APPLICATION_ENTITY_FIXTURE_1.apiKey,
            ),
        ).isTrue
    }

    @Test
    fun `isApiKeyValid returns true if no app is found`() {
        whenever(applicationRepository.findByName(APPLICATION_ENTITY_FIXTURE_1.name))
            .thenReturn(null)

        assertThat(
            applicationService.isApiKeyValid(
                APPLICATION_ENTITY_FIXTURE_1.name,
                APPLICATION_ENTITY_FIXTURE_1.apiKey,
            ),
        )
            .isFalse
    }

    @Test
    fun `isApiKeyValid returns false if api key does not match api key`() {
        whenever(applicationRepository.findByName(APPLICATION_ENTITY_FIXTURE_1.name))
            .thenReturn(APPLICATION_ENTITY_FIXTURE_1)

        assertThat(applicationService.isApiKeyValid(APPLICATION_ENTITY_FIXTURE_1.name, "something-else"))
            .isFalse
    }

    @Test
    fun `isQuarantined returns whether the app is in quarantine`() {
        whenever(applicationRepository.findByName(APPLICATION_ENTITY_FIXTURE_1.name))
            .thenReturn(APPLICATION_ENTITY_FIXTURE_1)

        assertThat(applicationService.isQuarantined(APPLICATION_ENTITY_FIXTURE_1.name))
            .isEqualTo(APPLICATION_ENTITY_FIXTURE_1.inQuarantine)
    }

    @Test
    fun `findByName calls findByName on the repository`() {
        whenever(applicationRepository.findByName(APPLICATION_ENTITY_FIXTURE_1.name))
            .thenReturn(APPLICATION_ENTITY_FIXTURE_WITH_ID_1)

        assertThat(applicationService.findByName(APPLICATION_ENTITY_FIXTURE_1.name))
            .isEqualTo(APPLICATION_FIXTURE_WITH_ID_1)
    }

    @Nested
    inner class CreateApplicationInputTest {
        @Test
        fun `name should not be blank`() {
            val exception =
                assertThrows<FunctionalException> {
                    CREATE_APPLICATION_FIXTURE_1.copy(name = " ")
                }
            assertThat(exception.message).isEqualTo("Name is blank")
        }

        @Test
        fun `name should not contain spaces`() {
            val exception =
                assertThrows<FunctionalException> {
                    CREATE_APPLICATION_FIXTURE_1.copy(name = "x y")
                }
            assertThat(exception.message).isEqualTo("Name contains spaces")
        }

        @Test
        fun `other names are allowed`() {
            assertDoesNotThrow {
                CREATE_APPLICATION_FIXTURE_1.copy(name = "abc")
            }
        }
    }
}
