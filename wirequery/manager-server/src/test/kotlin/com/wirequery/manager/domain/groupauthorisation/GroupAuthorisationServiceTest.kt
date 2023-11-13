package com.wirequery.manager.domain.groupauthorisation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GroupAuthorisationServiceTest {
    private val groupAuthorisationService = GroupAuthorisationService()

    @Test
    fun `findByNames returns all group authorisations matched by name`() {
        val actual = groupAuthorisationService.findByNames(setOf("VIEW_GROUP"))

        assertThat(actual.size).isEqualTo(1)

        assertThat(actual[0])
            .isEqualTo(
                GroupAuthorisation(
                    name = GroupAuthorisationEnum.VIEW_GROUP.name,
                    label = GroupAuthorisationEnum.VIEW_GROUP.label,
                    description = GroupAuthorisationEnum.VIEW_GROUP.description,
                ),
            )
    }

    @Test
    fun `findAll returns all group authorisations`() {
        val actual = groupAuthorisationService.findAll()

        assertThat(actual.size).isEqualTo(GroupAuthorisationEnum.entries.size)

        assertThat(actual)
            .contains(
                GroupAuthorisation(
                    name = GroupAuthorisationEnum.VIEW_GROUP.name,
                    label = GroupAuthorisationEnum.VIEW_GROUP.label,
                    description = GroupAuthorisationEnum.VIEW_GROUP.description,
                ),
            )
    }
}
