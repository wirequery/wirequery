// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.application

import com.wirequery.manager.IntegrationTestContext
import com.wirequery.manager.domain.application.ApplicationFixtures.CREATE_APPLICATION_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationFixtures.UPDATE_APPLICATION_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationService.UnquarantineApplicationInput
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ApplicationIntegrationTests : IntegrationTestContext() {
    @Autowired
    private lateinit var applicationService: ApplicationService

    @Test
    fun `Applications can be created, updated, fetched, quarantined, unquarantined, api key checked and deleted`() {
        var application = applicationService.create(CREATE_APPLICATION_FIXTURE_1)
        application = applicationService.update(application.id, UPDATE_APPLICATION_FIXTURE_1)!!

        assertThat(applicationService.findAll()).isNotEmpty
        assertThat(applicationService.findById(application.id)).isNotNull
        assertThat(applicationService.findByIds(listOf(application.id))).isNotEmpty

        assertThat(applicationService.isApiKeyValid(application.name, application.apiKey)).isTrue

        applicationService.quarantine(application.name, "some-rule", "some-reason")
        applicationService.unquarantine(application.id, UnquarantineApplicationInput("some-reason"))

        applicationService.deleteById(application.id)

        assertThat(applicationService.findAll()).isEmpty()
    }
}
