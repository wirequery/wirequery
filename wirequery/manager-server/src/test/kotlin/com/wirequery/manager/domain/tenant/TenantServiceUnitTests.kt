package com.wirequery.manager.domain.tenant

import com.wirequery.manager.domain.tenant.TenantFixtures.CREATE_TENANT_FIXTURE_1
import com.wirequery.manager.domain.tenant.TenantFixtures.TENANT_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.tenant.TenantFixtures.TENANT_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.tenant.TenantFixtures.TENANT_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.tenant.TenantFixtures.UPDATE_TENANT_FIXTURE_1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class TenantServiceUnitTests {
    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var tenantRepository: TenantRepository

    @Mock
    private lateinit var tenantRequestContext: TenantRequestContext

    @InjectMocks
    private lateinit var tenantService: TenantService

    @Test
    fun `tenantId is bound to tenantRequestContext`() {
        whenever(tenantRequestContext.tenantId)
            .thenReturn(10)
        assertThat(tenantService.tenantId).isEqualTo(10)
        tenantService.tenantId = 5
        verify(tenantRequestContext).tenantId = 5
    }

    @Test
    fun `findById returns the contained value of findById in TenantRepository if it is non-empty`() {
        whenever(tenantRepository.findById(1))
            .thenReturn(Optional.of(TENANT_ENTITY_FIXTURE_WITH_ID_1))

        val actual = tenantService.findById(1)

        assertThat(actual).isEqualTo(TENANT_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findById returns null if findById in TenantRepository yields an empty Optional`() {
        whenever(tenantRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = tenantService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findBySlug returns the contained value of findBySlug in TenantRepository if it is non-empty`() {
        whenever(tenantRepository.findBySlug("abc"))
            .thenReturn(TENANT_ENTITY_FIXTURE_WITH_ID_1)

        val actual = tenantService.findBySlug("abc")

        assertThat(actual).isEqualTo(TENANT_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findBySlug returns null if findBySlug in TenantRepository yields an empty Optional`() {
        whenever(tenantRepository.findBySlug("abc"))
            .thenReturn(null)

        val actual = tenantService.findBySlug("abc")

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findByIds returns the contained values of findByIds in TenantRepository`() {
        whenever(tenantRepository.findByIds(listOf(1)))
            .thenReturn(listOf(TENANT_ENTITY_FIXTURE_WITH_ID_1))

        val actual = tenantService.findByIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(TENANT_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findAll returns the values of findAll in TenantRepository`() {
        whenever(tenantRepository.findAll())
            .thenReturn(listOf(TENANT_ENTITY_FIXTURE_WITH_ID_1))

        val actual = tenantService.findAll()

        assertThat(actual).containsExactly(TENANT_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `create calls save on repository if all requirements are met and publishes an event`() {
        whenever(tenantRepository.save(TENANT_ENTITY_FIXTURE_1))
            .thenReturn(TENANT_ENTITY_FIXTURE_WITH_ID_1)

        val actual = tenantService.create(CREATE_TENANT_FIXTURE_1)

        assertThat(actual).isEqualTo(TENANT_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(TenantEvent.TenantsCreatedEvent(tenantService, listOf(TENANT_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `update calls save on repository if all requirements are met and publishes an event`() {
        whenever(tenantRepository.findById(1))
            .thenReturn(Optional.of(TENANT_ENTITY_FIXTURE_WITH_ID_1))

        whenever(tenantRepository.save(TENANT_ENTITY_FIXTURE_WITH_ID_1))
            .thenReturn(TENANT_ENTITY_FIXTURE_WITH_ID_1)

        val actual = tenantService.update(1, UPDATE_TENANT_FIXTURE_1)

        assertThat(actual).isEqualTo(TENANT_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(TenantEvent.TenantsUpdatedEvent(tenantService, listOf(TENANT_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `deleteById deletes the Tenant identified by id in the repository if it exists and publishes an event`() {
        whenever(tenantRepository.findById(1))
            .thenReturn(Optional.of(TENANT_ENTITY_FIXTURE_WITH_ID_1))

        tenantService.deleteById(1)

        verify(tenantRepository).deleteById(1)

        verify(publisher)
            .publishEvent(TenantEvent.TenantsDeletedEvent(tenantService, listOf(TENANT_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `deleteById does not delete the Tenant identified by id in the repository if it doesn't exist and publishes no events`() {
        whenever(tenantRepository.findById(1))
            .thenReturn(Optional.empty())

        tenantService.deleteById(1)

        verify(tenantRepository, times(0)).deleteById(1)
        verify(publisher, times(0)).publishEvent(any())
    }
}
