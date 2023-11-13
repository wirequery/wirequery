package com.wirequery.manager.domain.authorisation

import com.wirequery.manager.domain.authorisation.AuthorisationEnum.VIEW_APPLICATIONS
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorisationServiceTest {
    private val authorisationService = AuthorisationService()

    @Test
    fun `findByNames returns all authorisations matched by name`() {
        val actual = authorisationService.findByNames(setOf("VIEW_APPLICATIONS"))

        assertThat(actual.size).isEqualTo(1)

        assertThat(actual[0])
            .isEqualTo(
                Authorisation(
                    name = VIEW_APPLICATIONS.name,
                    label = VIEW_APPLICATIONS.label,
                    description = VIEW_APPLICATIONS.description,
                ),
            )
    }

    @Test
    fun `findAll returns all authorisations`() {
        val actual = authorisationService.findAll()

        assertThat(actual.size).isEqualTo(AuthorisationEnum.entries.size)

        assertThat(actual)
            .contains(
                Authorisation(
                    name = VIEW_APPLICATIONS.name,
                    label = VIEW_APPLICATIONS.label,
                    description = VIEW_APPLICATIONS.description,
                ),
            )
    }
}
