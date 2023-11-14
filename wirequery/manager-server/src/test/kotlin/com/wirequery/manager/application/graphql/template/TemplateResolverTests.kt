// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.template

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.template.TemplateFixtures.CREATE_TEMPLATE_FIXTURE_1
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.template.TemplateFixtures.UPDATE_TEMPLATE_FIXTURE_1
import com.wirequery.manager.domain.template.TemplateService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.GrantedAuthority

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        TemplateResolver::class,
        TemplateDataLoader::class,
        GraphQLExceptionHandler::class,
    ],
)
class TemplateResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var templateService: TemplateService

    @Test
    fun `template can be fetched when user has VIEW_TEMPLATES authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_TEMPLATES.name }))

        whenever(templateService.findById(TEMPLATE_FIXTURE_WITH_ID_1.id))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1)

        val id =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "{ template(id: ${TEMPLATE_FIXTURE_WITH_ID_1.id}) { id } }",
                "data.template.id",
            )

        assertThat(id).isEqualTo(TEMPLATE_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `template cannot be fetched when user does not VIEW_TEMPLATES authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "{ template(id: ${TEMPLATE_FIXTURE_WITH_ID_1.id}) { id } }",
                    "data.template.id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateService, times(0)).findById(any())
    }

    @Test
    fun `templates can be fetched when user has VIEW_TEMPLATES authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_TEMPLATES.name }))

        whenever(templateService.findAll())
            .thenReturn(listOf(TEMPLATE_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ templates { id } }",
                "data.templates[*].id",
            )

        assertThat(ids).contains(TEMPLATE_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `templates cannot be fetched when user does not have VIEW_TEMPLATES authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ templates { id } }",
                    "data.templates[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateService, times(0)).findAll()
    }

    @Test
    fun `createTemplate calls create if user has CREATE_TEMPLATE and USER_AUTH_TEMPLATE authorisation for allowUserInitiation true`() {
        whenever(authenticationMock.authorities)
            .thenReturn(
                listOf(
                    GrantedAuthority { AuthorisationEnum.CREATE_TEMPLATE.name },
                    GrantedAuthority { AuthorisationEnum.USER_AUTH_TEMPLATE.name },
                ),
            )

        whenever(templateService.create(any()))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1.copy(allowUserInitiation = true))

        val createTemplateInput =
            mapOf(
                "name" to "Some name",
                "description" to "Some description",
                "fields" to
                    listOf(
                        mapOf(
                            "key" to "Some key",
                            "label" to "Some label",
                            "type" to "TEXT",
                        ),
                    ),
                "nameTemplate" to "Some nameTemplate",
                "descriptionTemplate" to "Some descriptionTemplate",
                "allowUserInitiation" to true,
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation createTemplate(\$input: CreateTemplateInput!) { createTemplate(input: \$input) { id } }",
                "data.createTemplate.id",
                mapOf("input" to createTemplateInput),
            )

        assertThat(result).isEqualTo(TEMPLATE_FIXTURE_WITH_ID_1.id.toString())

        verify(templateService).create(CREATE_TEMPLATE_FIXTURE_1)
    }

    @Test
    fun `createTemplate does not call create if user has CREATE_TEMPLATE and not USER_AUTH_TEMPLATE authorisation for allowUserInitiation true`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.CREATE_TEMPLATE.name }))

        val createTemplateInput =
            mapOf(
                "name" to "Some name",
                "description" to "Some description",
                "fields" to
                    listOf(
                        mapOf(
                            "key" to "Some key",
                            "label" to "Some label",
                            "type" to "TEXT",
                        ),
                    ),
                "nameTemplate" to "Some nameTemplate",
                "descriptionTemplate" to "Some descriptionTemplate",
                "allowUserInitiation" to true,
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation createTemplate(\$input: CreateTemplateInput!) { createTemplate(input: \$input) { id } }",
                    "data.createTemplate.id",
                    mapOf("input" to createTemplateInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateService, times(0)).create(any())
    }

    @Test
    fun `createTemplate calls create if user has CREATE_TEMPLATE authorisation for allowUserInitiation false`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.CREATE_TEMPLATE.name }))

        whenever(templateService.create(any()))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1.copy(allowUserInitiation = false))

        val createTemplateInput =
            mapOf(
                "name" to "Some name",
                "description" to "Some description",
                "fields" to
                    listOf(
                        mapOf(
                            "key" to "Some key",
                            "label" to "Some label",
                            "type" to "TEXT",
                        ),
                    ),
                "nameTemplate" to "Some nameTemplate",
                "descriptionTemplate" to "Some descriptionTemplate",
                "allowUserInitiation" to false,
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation createTemplate(\$input: CreateTemplateInput!) { createTemplate(input: \$input) { id } }",
                "data.createTemplate.id",
                mapOf("input" to createTemplateInput),
            )

        assertThat(result).isEqualTo(TEMPLATE_FIXTURE_WITH_ID_1.id.toString())

        verify(templateService).create(CREATE_TEMPLATE_FIXTURE_1.copy(allowUserInitiation = false))
    }

    @Test
    fun `createTemplate does not call create if user does not have has CREATE_TEMPLATE authorisation`() {
        val createTemplateInput =
            mapOf(
                "name" to "Some name",
                "description" to "Some description",
                "fields" to
                    listOf(
                        mapOf(
                            "key" to "Some key",
                            "label" to "Some label",
                            "type" to "TEXT",
                        ),
                    ),
                "nameTemplate" to "Some nameTemplate",
                "descriptionTemplate" to "Some descriptionTemplate",
                "allowUserInitiation" to true,
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation createTemplate(\$input: CreateTemplateInput!) { createTemplate(input: \$input) { id } }",
                    "data.createTemplate.id",
                    mapOf("input" to createTemplateInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateService, times(0)).create(CREATE_TEMPLATE_FIXTURE_1)
    }

    @Test
    fun `updateTemplate calls update if the user has UPDATE_TEMPLATE and USER_AUTH_TEMPLATE authorisation for allowUserInitiation true`() {
        whenever(authenticationMock.authorities)
            .thenReturn(
                listOf(
                    GrantedAuthority { AuthorisationEnum.UPDATE_TEMPLATE.name },
                    GrantedAuthority { AuthorisationEnum.USER_AUTH_TEMPLATE.name },
                ),
            )

        whenever(templateService.update(anyInt(), any()))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1.copy(allowUserInitiation = true))

        val updateTemplateInput =
            mapOf(
                "name" to "Some name",
                "description" to "Some description",
                "fields" to
                    listOf(
                        mapOf(
                            "key" to "Some key",
                            "label" to "Some label",
                            "type" to "TEXT",
                        ),
                    ),
                "nameTemplate" to "Some nameTemplate",
                "descriptionTemplate" to "Some descriptionTemplate",
                "allowUserInitiation" to true,
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation updateTemplate(\$id: ID!, \$input: UpdateTemplateInput!) { updateTemplate(id: \$id, input: \$input) { id } }",
                "data.updateTemplate.id",
                mapOf("id" to TEMPLATE_FIXTURE_WITH_ID_1.id, "input" to updateTemplateInput),
            )

        assertThat(result).isEqualTo(TEMPLATE_FIXTURE_WITH_ID_1.id.toString())

        verify(templateService).update(TEMPLATE_FIXTURE_WITH_ID_1.id, UPDATE_TEMPLATE_FIXTURE_1)
    }

    @Test
    fun `updateTemplate does not call update if the user has UPDATE_TEMPLATE but not USER_AUTH_TEMPLATE authorisation for allowUserInitiation true`() {
        whenever(authenticationMock.authorities)
            .thenReturn(
                listOf(
                    GrantedAuthority { AuthorisationEnum.UPDATE_TEMPLATE.name },
                ),
            )

        val updateTemplateInput =
            mapOf(
                "name" to "Some name",
                "description" to "Some description",
                "fields" to
                    listOf(
                        mapOf(
                            "key" to "Some key",
                            "label" to "Some label",
                            "type" to "TEXT",
                        ),
                    ),
                "nameTemplate" to "Some nameTemplate",
                "descriptionTemplate" to "Some descriptionTemplate",
                "allowUserInitiation" to true,
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation updateTemplate(\$id: ID!, \$input: UpdateTemplateInput!) { updateTemplate(id: \$id, input: \$input) { id } }",
                    "data.updateTemplate.id",
                    mapOf("id" to TEMPLATE_FIXTURE_WITH_ID_1.id, "input" to updateTemplateInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")
        verify(templateService, times(0))
            .update(anyInt(), any())
    }

    @Test
    fun `updateTemplate calls update if the user has UPDATE_TEMPLATE authorisation for allowUserInitiation false`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.UPDATE_TEMPLATE.name }))

        whenever(templateService.update(anyInt(), any()))
            .thenReturn(TEMPLATE_FIXTURE_WITH_ID_1.copy(allowUserInitiation = false))

        val updateTemplateInput =
            mapOf(
                "name" to "Some name",
                "description" to "Some description",
                "fields" to
                    listOf(
                        mapOf(
                            "key" to "Some key",
                            "label" to "Some label",
                            "type" to "TEXT",
                        ),
                    ),
                "nameTemplate" to "Some nameTemplate",
                "descriptionTemplate" to "Some descriptionTemplate",
                "allowUserInitiation" to false,
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation updateTemplate(\$id: ID!, \$input: UpdateTemplateInput!) { updateTemplate(id: \$id, input: \$input) { id } }",
                "data.updateTemplate.id",
                mapOf("id" to TEMPLATE_FIXTURE_WITH_ID_1.id, "input" to updateTemplateInput),
            )

        assertThat(result).isEqualTo(TEMPLATE_FIXTURE_WITH_ID_1.id.toString())

        verify(templateService).update(TEMPLATE_FIXTURE_WITH_ID_1.id, UPDATE_TEMPLATE_FIXTURE_1.copy(allowUserInitiation = false))
    }

    @Test
    fun `updateTemplate does not call update if the user does not have UPDATE_TEMPLATE authorisation`() {
        val updateTemplateInput =
            mapOf(
                "name" to "Some name",
                "description" to "Some description",
                "fields" to
                    listOf(
                        mapOf(
                            "key" to "Some key",
                            "label" to "Some label",
                            "type" to "TEXT",
                        ),
                    ),
                "nameTemplate" to "Some nameTemplate",
                "descriptionTemplate" to "Some descriptionTemplate",
                "allowUserInitiation" to true,
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation updateTemplate(\$id: ID!, \$input: UpdateTemplateInput!) { updateTemplate(id: \$id, input: \$input) { id } }",
                    "data.updateTemplate.id",
                    mapOf("id" to TEMPLATE_FIXTURE_WITH_ID_1.id, "input" to updateTemplateInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateService, times(0)).update(anyInt(), any())
    }

    @Test
    fun `deleteTemplate calls delete if the user has DELETE_TEMPLATE authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.DELETE_TEMPLATE.name }))

        whenever(templateService.deleteById(any()))
            .thenReturn(true)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                "mutation deleteTemplate(\$id: ID!) { deleteTemplate(id: \$id) }",
                "data.deleteTemplate",
                mapOf("id" to 1),
            )

        assertThat(result).isEqualTo(true)

        verify(templateService).deleteById(1)
    }

    @Test
    fun `deleteTemplate does not call delete if the user does not have DELETE_TEMPLATE authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                    "mutation deleteTemplate(\$id: ID!) { deleteTemplate(id: \$id) }",
                    "data.deleteTemplate",
                    mapOf("id" to 1),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateService, times(0)).deleteById(anyInt())
    }
}
