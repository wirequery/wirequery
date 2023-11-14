// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.role

import com.wirequery.manager.domain.FunctionalException
import com.wirequery.manager.domain.role.RoleFixtures.CREATE_ROLE_FIXTURE_1
import com.wirequery.manager.domain.role.RoleFixtures.ROLE_ENTITY_FIXTURE_1
import com.wirequery.manager.domain.role.RoleFixtures.ROLE_ENTITY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.role.RoleFixtures.ROLE_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.role.RoleFixtures.UPDATE_ROLE_FIXTURE_1
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
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class RoleServiceUnitTests {
    @Mock
    private lateinit var publisher: ApplicationEventPublisher

    @Mock
    private lateinit var roleRepository: RoleRepository

    @InjectMocks
    private lateinit var roleService: RoleService

    @Test
    fun `findById returns the mapped value of findById in RoleRepository if it is non-empty`() {
        whenever(roleRepository.findById(1))
            .thenReturn(Optional.of(ROLE_ENTITY_FIXTURE_WITH_ID_1))

        val actual = roleService.findById(1)

        assertThat(actual).isEqualTo(ROLE_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findById returns null if findById in RoleRepository yields an empty Optional`() {
        whenever(roleRepository.findById(1))
            .thenReturn(Optional.empty())

        val actual = roleService.findById(1)

        assertThat(actual).isNull()

        verify(publisher, times(0))
            .publishEvent(any())
    }

    @Test
    fun `findByIds returns the mapped values of findByIds in RoleRepository`() {
        whenever(roleRepository.findByIds(listOf(1)))
            .thenReturn(listOf(ROLE_ENTITY_FIXTURE_WITH_ID_1))

        val actual = roleService.findByIds(listOf(1))

        assertThat(actual).isEqualTo(listOf(ROLE_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findAll returns the values of findAll in RoleRepository`() {
        whenever(roleRepository.findAll())
            .thenReturn(listOf(ROLE_ENTITY_FIXTURE_WITH_ID_1))

        val actual = roleService.findAll()

        assertThat(actual).containsExactly(ROLE_FIXTURE_WITH_ID_1)
    }

    @Test
    fun `findByNames returns empty list if nothing provided`() {
        val actual = roleService.findByNames(setOf())

        assertThat(actual).isEqualTo(setOf<Role>())

        verify(roleRepository, times(0)).findAll()
    }

    @Test
    fun `findByNames returns matching elements`() {
        whenever(roleRepository.findAll())
            .thenReturn(listOf(ROLE_ENTITY_FIXTURE_WITH_ID_1))

        val actual = roleService.findByNames(setOf("Some role"))

        assertThat(actual).isEqualTo(setOf(ROLE_FIXTURE_WITH_ID_1))
    }

    @Test
    fun `findByNames does not return non-matching elements`() {
        whenever(roleRepository.findAll())
            .thenReturn(listOf(ROLE_ENTITY_FIXTURE_WITH_ID_1))

        val actual = roleService.findByNames(setOf("Some other role"))

        assertThat(actual).isEqualTo(setOf<Role>())
    }

    @Test
    fun `create calls save on repository if all requirements are met and publishes an event`() {
        whenever(roleRepository.save(ROLE_ENTITY_FIXTURE_1))
            .thenReturn(ROLE_ENTITY_FIXTURE_WITH_ID_1)

        val actual = roleService.create(CREATE_ROLE_FIXTURE_1)

        assertThat(actual).isEqualTo(ROLE_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(RoleEvent.RolesCreatedEvent(roleService, listOf(ROLE_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `update calls save on repository if all requirements are met and publishes an event`() {
        whenever(roleRepository.findById(1))
            .thenReturn(Optional.of(ROLE_ENTITY_FIXTURE_WITH_ID_1))

        whenever(roleRepository.save(ROLE_ENTITY_FIXTURE_WITH_ID_1))
            .thenReturn(ROLE_ENTITY_FIXTURE_WITH_ID_1)

        val actual = roleService.update(1, UPDATE_ROLE_FIXTURE_1)

        assertThat(actual).isEqualTo(ROLE_FIXTURE_WITH_ID_1)

        verify(publisher)
            .publishEvent(RoleEvent.RolesUpdatedEvent(roleService, listOf(ROLE_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `deleteById deletes the Role identified by id in the repository if it exists and publishes an event`() {
        whenever(roleRepository.findById(1))
            .thenReturn(Optional.of(ROLE_ENTITY_FIXTURE_WITH_ID_1))

        roleService.deleteById(1)

        verify(roleRepository).deleteById(1)

        verify(publisher)
            .publishEvent(RoleEvent.RolesDeletedEvent(roleService, listOf(ROLE_FIXTURE_WITH_ID_1)))
    }

    @Test
    fun `deleteById does not delete the Role identified by id in the repository if it doesn't exist and publishes no events`() {
        whenever(roleRepository.findById(1))
            .thenReturn(Optional.empty())

        roleService.deleteById(1)

        verify(roleRepository, times(0)).deleteById(1)
        verify(publisher, times(0)).publishEvent(any())
    }

    @Nested
    inner class CreateRoleInputTest {
        @Test
        fun `name should not be blank`() {
            val exception =
                assertThrows<FunctionalException> {
                    CREATE_ROLE_FIXTURE_1.copy(name = " ")
                }
            assertThat(exception.message).isEqualTo("Name is blank")
        }

        @Test
        fun `name should not contain a comma`() {
            val exception = assertThrows<FunctionalException> { CREATE_ROLE_FIXTURE_1.copy(name = ",") }
            assertThat(exception.message).isEqualTo("Name contains a comma")
        }

        @Test
        fun `other names are allowed`() {
            assertDoesNotThrow {
                CREATE_ROLE_FIXTURE_1.copy(name = "abc")
            }
        }
    }

    @Nested
    inner class UpdateRoleInputTest {
        @Test
        fun `name should not be blank`() {
            val exception =
                assertThrows<FunctionalException> {
                    UPDATE_ROLE_FIXTURE_1.copy(name = " ")
                }
            assertThat(exception.message).isEqualTo("Name is blank")
        }

        @Test
        fun `name should not contain a comma`() {
            val exception = assertThrows<FunctionalException> { UPDATE_ROLE_FIXTURE_1.copy(name = ",") }
            assertThat(exception.message).isEqualTo("Name contains a comma")
        }

        @Test
        fun `other names are allowed`() {
            assertDoesNotThrow {
                UPDATE_ROLE_FIXTURE_1.copy(name = "abc")
            }
        }

        @Test
        fun `null names are allowed`() {
            assertDoesNotThrow {
                UPDATE_ROLE_FIXTURE_1.copy(name = null)
            }
        }
    }
}
