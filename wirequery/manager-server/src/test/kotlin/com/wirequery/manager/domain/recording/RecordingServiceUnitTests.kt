package com.wirequery.manager.domain.recording

import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.recording.Recording.StatusEnum.CANCELLED
import com.wirequery.manager.domain.recording.Recording.StatusEnum.FINISHED
import com.wirequery.manager.domain.recording.RecordingFixtures.RECORDING_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.recording.RecordingFixtures.RECORDING_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.recording.RecordingFixtures.RECORDING_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.recording.RecordingFixtures.START_RECORDING_FIXTURE_1
import com.wirequery.manager.domain.recording.RecordingFixtures.UPDATE_RECORDING_FIXTURE_1
import com.wirequery.manager.domain.recording.RecordingService.Companion.RECORDING_TIMEOUT
import com.wirequery.manager.domain.session.SessionFixtures.SESSION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.session.SessionService
import com.wirequery.manager.domain.session.SessionService.CreateSessionInput
import com.wirequery.manager.domain.session.SessionService.CreateSessionInputFieldValue
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.template.TemplateService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class RecordingServiceUnitTests {
    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var recordingRepository: RecordingRepository

    @Mock
    private lateinit var sessionService: SessionService

    @Mock
    private lateinit var templateService: TemplateService

    @Mock
    private lateinit var recordingSecretGenerator: RecordingSecretGenerator

    @Mock
    private lateinit var clock: Clock

    @InjectMocks
    private lateinit var recordingService: RecordingService

    @Test
    fun `findById returns the mapped value of findById in RecordingRepository if it is non-empty`() {
        whenever(recordingRepository.findById(1))
            .thenReturn(Optional.of(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        val actual = recordingService.findById(1)

        assertThat(actual).isEqualTo(RECORDING_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findById returns null if findById in RecordingRepository yields an empty Optional`() {
        whenever(recordingRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = recordingService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findByIds returns the mapped values of findByIds in RecordingRepository`() {
        whenever(recordingRepository.findByIds(listOf(1)))
            .thenReturn(listOf(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        val actual = recordingService.findByIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(RECORDING_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findBySessionIds returns the contents of the corresponding repository call`() {
        whenever(recordingRepository.findBySessionIds(listOf(1)))
            .thenReturn(listOf(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        val actual = recordingService.findBySessionIds(listOf(1))

        assertThat(actual).isEqualTo(actual)
    }

    @Test
    fun `findByTemplateIds returns the contents of the corresponding repository call`() {
        whenever(recordingRepository.findByTemplateIds(listOf(1)))
            .thenReturn(listOf(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        val actual = recordingService.findByTemplateIds(listOf(1))

        assertThat(actual).isEqualTo(actual)
    }

    @Test
    fun `findAll delegates to findBySessionId when filter only contains sessionId`() {
        whenever(recordingRepository.findBySessionIds(listOf(1)))
            .thenReturn(listOf(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        val actual = recordingService.findAll(RecordingService.RecordingFilterInput(sessionId = 1))

        assertThat(actual).containsExactly(RECORDING_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `startRecording calls save on repository if all requirements are met and publishes an event`() {
        whenever(clock.zone).thenReturn(ZoneId.systemDefault())

        whenever(clock.instant()).thenReturn(Instant.now())

        whenever(
            recordingRepository.save(
                RECORDING_ENTITY_FIXTURE_1,
            ),
        )
            .thenReturn(RECORDING_ENTITY_FIXTURE_WITH_ID_1)

        whenever(recordingSecretGenerator.generate())
            .thenReturn(RECORDING_ENTITY_FIXTURE_1.secret)

        whenever(
            sessionService.create(
                CreateSessionInput(
                    templateId = START_RECORDING_FIXTURE_1.templateId,
                    variables =
                        START_RECORDING_FIXTURE_1.args.map {
                            CreateSessionInputFieldValue(it.key, it.value)
                        },
                    endDate = OffsetDateTime.now(clock).plusSeconds(RECORDING_TIMEOUT.toLong()),
                ),
                true,
            ),
        )
            .thenReturn(
                SESSION_FIXTURE_WITH_ID_1.copy(
                    id = RECORDING_ENTITY_FIXTURE_1.sessionId,
                ),
            )

        whenever(templateService.findById(RECORDING_FIXTURE_WITH_ID_1.templateId!!))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1)

        val actual = recordingService.startRecording(START_RECORDING_FIXTURE_1)

        assertThat(actual).isEqualTo(RECORDING_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(RecordingEvent.RecordingsCreatedEvent(recordingService, listOf(RECORDING_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `startRecording fails when template is not user initiatable and publishes no events`() {
        whenever(templateService.findById(RECORDING_FIXTURE_WITH_ID_1.templateId!!))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1.copy(allowUserInitiation = false))

        val error =
            assertThrows<FunctionalException> {
                recordingService.startRecording(START_RECORDING_FIXTURE_1)
            }
        assertThat(error.message).isEqualTo("Template with id ${RECORDING_FIXTURE_WITH_ID_1.templateId} does not allow user initiation.")

        verify(recordingRepository, times(0)).save(RECORDING_ENTITY_FIXTURE_1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `startRecording fails when related objects cannot be found and publishes no events`() {
        assertThrows<RuntimeException> {
            recordingService.startRecording(START_RECORDING_FIXTURE_1)
        }

        verify(recordingRepository, times(0)).save(RECORDING_ENTITY_FIXTURE_1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `verifySecret returns true if the secret matches the one in the entity`() {
        whenever(recordingRepository.findById(RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!))
            .thenReturn(Optional.of(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        assertThat(
            recordingService.verifySecret(
                RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!,
                RECORDING_ENTITY_FIXTURE_WITH_ID_1.secret,
            ),
        )
            .isEqualTo(true)
    }

    @Test
    fun `verifySecret returns false if the secret does not match the one in the entity`() {
        whenever(recordingRepository.findById(RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!))
            .thenReturn(Optional.of(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        assertThat(recordingService.verifySecret(RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!, "some other secret"))
            .isEqualTo(false)
    }

    @Test
    fun `verifySecret returns false if the related entity does not exist`() {
        whenever(recordingRepository.findById(RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!))
            .thenReturn(Optional.empty())

        assertThat(recordingService.verifySecret(RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!, "some other secret"))
            .isEqualTo(false)
    }

    @Test
    fun `cancelRecording deletes the related session and sets status to CANCELLED`() {
        whenever(recordingRepository.findById(RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!))
            .thenReturn(Optional.of(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        recordingService.cancelRecording(RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!)

        verify(recordingRepository).save(RECORDING_ENTITY_FIXTURE_WITH_ID_1.copy(status = CANCELLED))

        verify(sessionService).stopCapturing(RECORDING_ENTITY_FIXTURE_WITH_ID_1.sessionId, false)

        verify(sessionService).deleteById(RECORDING_ENTITY_FIXTURE_WITH_ID_1.sessionId)
    }

    @Test
    fun `finishRecording updates recording and stops session`() {
        whenever(recordingRepository.findById(RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!))
            .thenReturn(Optional.of(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        recordingService.finishRecording(
            RECORDING_ENTITY_FIXTURE_WITH_ID_1.id!!,
            RECORDING_ENTITY_FIXTURE_WITH_ID_1.recording,
        )

        verify(recordingRepository).save(
            RECORDING_ENTITY_FIXTURE_WITH_ID_1.copy(
                status = FINISHED,
                recording = "",
            ),
        )

        verify(sessionService)
            .stopCapturing(RECORDING_ENTITY_FIXTURE_WITH_ID_1.sessionId, true)
    }

    @Test
    fun `update calls save on repository if all requirements are met and publishes an event`() {
        whenever(recordingRepository.findById(1))
            .thenReturn(Optional.of(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        whenever(recordingRepository.save(RECORDING_ENTITY_FIXTURE_WITH_ID_1))
            .thenReturn(RECORDING_ENTITY_FIXTURE_WITH_ID_1)

        whenever(sessionService.findById(RECORDING_ENTITY_FIXTURE_WITH_ID_1.sessionId))
            .thenReturn(SESSION_FIXTURE_WITH_ID_1)

        whenever(templateService.findById(RECORDING_ENTITY_FIXTURE_WITH_ID_1.templateId!!))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1)

        val actual = recordingService.update(1, UPDATE_RECORDING_FIXTURE_1)

        assertThat(actual).isEqualTo(RECORDING_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(RecordingEvent.RecordingsUpdatedEvent(recordingService, listOf(RECORDING_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `update fails when related objects cannot be found and publishes no events`() {
        assertThrows<RuntimeException> {
            recordingService.update(1, UPDATE_RECORDING_FIXTURE_1)
        }

        verify(recordingRepository, times(0)).save(RECORDING_ENTITY_FIXTURE_WITH_ID_1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `deleteById deletes the Recording identified by id in the repository if it exists and publishes an event`() {
        whenever(recordingRepository.findById(1))
            .thenReturn(Optional.of(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        recordingService.deleteById(1)

        verify(recordingRepository).deleteById(1)

        verify(publisher)
            .publishEvent(RecordingEvent.RecordingsDeletedEvent(recordingService, listOf(RECORDING_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `deleteById does not delete the Recording identified by id in the repository if it doesn't exist and publishes no events`() {
        whenever(recordingRepository.findById(1))
            .thenReturn(Optional.empty())

        recordingService.deleteById(1)

        verify(recordingRepository, times(0)).deleteById(1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `deleteBySessionId deletes sessions by sessionId and publishes event`() {
        whenever(recordingRepository.findBySessionIds(listOf(10)))
            .thenReturn(listOf(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        val actual = recordingService.deleteBySessionIds(listOf(10))
        assertThat(actual).isEqualTo(true)

        verify(recordingRepository).deleteAll(listOf(RECORDING_ENTITY_FIXTURE_WITH_ID_1))
        verify(publisher).publishEvent(
            RecordingEvent.RecordingsDeletedEvent(
                recordingService,
                listOf(RECORDING_FIXTURE_WITH_ID_1),
            ),
        )
    }

    @Test
    fun `deleteBySessionId returns false if no sessions are deleted and publishes no events`() {
        whenever(recordingRepository.findBySessionIds(listOf(10)))
            .thenReturn(listOf())

        val actual = recordingService.deleteBySessionIds(listOf(10))
        assertThat(actual).isEqualTo(false)

        verify(recordingRepository, times(0)).deleteAll(any())
        verify(publisher, times(0)).publishEvent(any())
    }

    @Test
    fun `deleteByTemplateId deletes templates by templateId and publishes event`() {
        whenever(recordingRepository.findByTemplateIds(listOf(10)))
            .thenReturn(listOf(RECORDING_ENTITY_FIXTURE_WITH_ID_1))

        val actual = recordingService.deleteByTemplateIds(listOf(10))
        assertThat(actual).isEqualTo(true)

        verify(recordingRepository).deleteAll(listOf(RECORDING_ENTITY_FIXTURE_WITH_ID_1))
        verify(publisher).publishEvent(
            RecordingEvent.RecordingsDeletedEvent(
                recordingService,
                listOf(RECORDING_FIXTURE_WITH_ID_1),
            ),
        )
    }

    @Test
    fun `deleteByTemplateId returns false if no templates are deleted and publishes no events`() {
        whenever(recordingRepository.findByTemplateIds(listOf(10)))
            .thenReturn(listOf())

        val actual = recordingService.deleteByTemplateIds(listOf(10))
        assertThat(actual).isEqualTo(false)

        verify(recordingRepository, times(0)).deleteAll(any())
        verify(publisher, times(0)).publishEvent(any())
    }
}
