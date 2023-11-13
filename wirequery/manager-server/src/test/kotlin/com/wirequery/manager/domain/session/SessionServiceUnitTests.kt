package com.wirequery.manager.domain.session

import com.wirequery.manager.domain.session.SessionFixtures.CREATE_SESSION_FIXTURE_1
import com.wirequery.manager.domain.session.SessionFixtures.OFFSET_DATE_TIME_FIXTURE
import com.wirequery.manager.domain.session.SessionFixtures.SESSION_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.session.SessionFixtures.SESSION_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.session.SessionFixtures.SESSION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.STORED_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryService
import com.wirequery.manager.domain.template.TemplateFixtures
import com.wirequery.manager.domain.template.TemplateService
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.TEMPLATE_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.templatequery.TemplateQueryService
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
internal class SessionServiceUnitTests {
    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var templateService: TemplateService

    @Mock
    private lateinit var templateQueryService: TemplateQueryService

    @Mock
    private lateinit var storedQueryService: StoredQueryService

    @Mock
    private lateinit var querySessionRepository: QuerySessionRepository

    @InjectMocks
    private lateinit var sessionService: SessionService

    @Test
    fun `findById returns the contained value of findById in SessionRepository if it is non-empty`() {
        whenever(querySessionRepository.findById(1))
            .thenReturn(Optional.of(SESSION_ENTITY_FIXTURE_WITH_ID_1))

        val actual = sessionService.findById(1)

        assertThat(actual).isEqualTo(SESSION_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findById returns null if findById in SessionRepository yields an empty Optional`() {
        whenever(querySessionRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = sessionService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findByIds returns the contained values of findByIds in SessionRepository`() {
        whenever(querySessionRepository.findByIds(listOf(1)))
            .thenReturn(listOf(SESSION_ENTITY_FIXTURE_WITH_ID_1))

        val actual = sessionService.findByIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(SESSION_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findAll returns the values of findByNonDraft in SessionRepository`() {
        whenever(querySessionRepository.findByNonDraft())
            .thenReturn(listOf(SESSION_ENTITY_FIXTURE_WITH_ID_1))

        val actual = sessionService.findAll()

        assertThat(actual).containsExactly(SESSION_FIXTURE_WITH_ID_1)
    }

    @ParameterizedTest
    @CsvSource("true", "false")
    fun `create calls save on repository if all requirements are met and publishes an event`(draft: Boolean) {
        whenever(templateQueryService.findByTemplateIds(listOf(CREATE_SESSION_FIXTURE_1.templateId)))
            .thenReturn(listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1))

        whenever(templateService.findById(CREATE_SESSION_FIXTURE_1.templateId))
            .thenReturn(
                TemplateFixtures.TEMPLATE_FIXTURE_WITH_ID_1.copy(
                    nameTemplate = SESSION_FIXTURE_WITH_ID_1.name,
                    descriptionTemplate = SESSION_FIXTURE_WITH_ID_1.description,
                ),
            )

        whenever(querySessionRepository.save(SESSION_ENTITY_FIXTURE_1.copy(draft = draft)))
            .thenReturn(SESSION_ENTITY_FIXTURE_WITH_ID_1.copy(draft = draft))

        val actual = sessionService.create(CREATE_SESSION_FIXTURE_1.copy(endDate = OFFSET_DATE_TIME_FIXTURE.plusDays(1)), draft = draft)

        assertThat(actual).isEqualTo(SESSION_FIXTURE_WITH_ID_1.copy(draft = draft))

        verify(storedQueryService).create(
            StoredQueryService.CreateStoredQueryInput(
                sessionId = SESSION_FIXTURE_WITH_ID_1.id,
                name = "Some nameTemplate",
                type = "TAPPING",
                query = "Some queryTemplate",
                queryLimit = 1,
                endDate = OFFSET_DATE_TIME_FIXTURE.plusDays(1),
            ),
        )

        verify(publisher)
            .publishEvent(
                SessionEvent.SessionsCreatedEvent(
                    sessionService,
                    listOf(SESSION_FIXTURE_WITH_ID_1.copy(draft = draft)),
                    mapOf(SESSION_FIXTURE_WITH_ID_1.copy(draft = draft) to CREATE_SESSION_FIXTURE_1.templateId),
                ),
            )
    }

    @Test
    fun `deleteOldDrafts deletes old drafts`() {
        whenever(querySessionRepository.findDrafts())
            .thenReturn(listOf(SESSION_ENTITY_FIXTURE_WITH_ID_1))

        sessionService.deleteOldDrafts()

        verify(querySessionRepository).deleteAll(listOf(SESSION_ENTITY_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `stopCapturing calls each app to stop querying and does not set draft to false if publish is false`() {
        whenever(storedQueryService.findBySessionIds(listOf(SESSION_FIXTURE_WITH_ID_1.id)))
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1))

        sessionService.stopCapturing(SESSION_FIXTURE_WITH_ID_1.id, false)

        verify(storedQueryService)
            .stopQueryingByApplicationIds(listOf(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId))

        verifyNoMoreInteractions(querySessionRepository)
    }

    @Test
    fun `stopCapturing calls each app to stop querying and sets draft to false if publish is true`() {
        whenever(storedQueryService.findBySessionIds(listOf(SESSION_FIXTURE_WITH_ID_1.id)))
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1))

        whenever(querySessionRepository.findById(SESSION_FIXTURE_WITH_ID_1.id))
            .thenReturn(Optional.of(SESSION_ENTITY_FIXTURE_WITH_ID_1.copy(draft = false)))

        sessionService.stopCapturing(SESSION_FIXTURE_WITH_ID_1.id, true)

        verify(storedQueryService)
            .stopQueryingByApplicationIds(listOf(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId))

        verify(querySessionRepository)
            .save(SESSION_ENTITY_FIXTURE_WITH_ID_1.copy(draft = false))
    }

    @Test
    fun `deleteById deletes the Session identified by id in the repository if it exists and publishes an event`() {
        whenever(querySessionRepository.findById(1))
            .thenReturn(Optional.of(SESSION_ENTITY_FIXTURE_WITH_ID_1))

        sessionService.deleteById(1)

        verify(querySessionRepository).deleteById(1)

        verify(publisher)
            .publishEvent(SessionEvent.SessionsDeletedEvent(sessionService, listOf(SESSION_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `deleteById does not delete the Session identified by id in the repository if it doesn't exist and publishes no events`() {
        whenever(querySessionRepository.findById(1))
            .thenReturn(Optional.empty())

        sessionService.deleteById(1)

        verify(querySessionRepository, times(0)).deleteById(1)
        verify(publisher, times(0)).publishEvent(any())
    }
}
