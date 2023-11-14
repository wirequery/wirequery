// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.templatequery

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.application.graphql.application.ApplicationByIdDataLoader
import com.wirequery.manager.application.graphql.application.ApplicationResolver
import com.wirequery.manager.application.graphql.template.TemplateDataLoader
import com.wirequery.manager.application.graphql.template.TemplateResolver
import com.wirequery.manager.domain.access.AccessService
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum
import com.wirequery.manager.domain.template.TemplateFixtures.TEMPLATE_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.template.TemplateService
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.CREATE_TEMPLATE_QUERY_FIXTURE_1
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.TEMPLATE_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.templatequery.TemplateQueryFixtures.UPDATE_TEMPLATE_QUERY_FIXTURE_1
import com.wirequery.manager.domain.templatequery.TemplateQueryService
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
        ApplicationResolver::class,
        ApplicationByIdDataLoader::class,
        TemplateQueryResolver::class,
        TemplateQueryDataLoader::class,
        AccessService::class,
        GraphQLExceptionHandler::class,
    ],
)
class TemplateQueryResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var templateService: TemplateService

    @MockBean
    private lateinit var applicationService: ApplicationService

    @MockBean
    private lateinit var templateQueryService: TemplateQueryService

    @MockBean
    private lateinit var accessService: AccessService

    // TODO templateQuery

    @Test
    fun `templateQuerys can be fetched when user has VIEW_TEMPLATES authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_TEMPLATES.name }))

        whenever(templateQueryService.findAll())
            .thenReturn(listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ templateQuerys { id } }",
                "data.templateQuerys[*].id",
            )

        assertThat(ids).contains(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `templateQuerys cannot be fetched when user does not have VIEW_TEMPLATES authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ templateQuerys { id } }",
                    "data.templateQuerys[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateQueryService, times(0)).findAll()
    }

    @Test
    fun `createTemplateQuery calls create if user has CREATE_TEMPLATE authorisation and the expression is allowed`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.CREATE_TEMPLATE.name }))

        whenever(templateQueryService.create(any()))
            .thenReturn(TEMPLATE_QUERY_FIXTURE_WITH_ID_1)

        whenever(accessService.isExpressionTemplateAllowed("Some queryTemplate", GroupAuthorisationEnum.CREATE_OR_EDIT_TEMPLATE_QUERY))
            .thenReturn(true)

        val createTemplateQueryInput =
            mapOf(
                "templateId" to 10,
                "nameTemplate" to "Some nameTemplate",
                "queryTemplate" to "Some queryTemplate",
                "queryLimit" to 1,
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation createTemplateQuery(\$input: CreateTemplateQueryInput!) { createTemplateQuery(input: \$input) { id } }",
                "data.createTemplateQuery.id",
                mapOf("input" to createTemplateQueryInput),
            )

        assertThat(result).isEqualTo(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.id.toString())

        verify(templateQueryService).create(CREATE_TEMPLATE_QUERY_FIXTURE_1)
    }

    @Test
    fun `createTemplateQuery does not call create if user does not have CREATE_TEMPLATE authorisation`() {
        whenever(accessService.isExpressionTemplateAllowed("Some queryTemplate", GroupAuthorisationEnum.CREATE_OR_EDIT_TEMPLATE_QUERY))
            .thenReturn(true)

        val createTemplateQueryInput =
            mapOf(
                "templateId" to 10,
                "nameTemplate" to "Some nameTemplate",
                "queryTemplate" to "Some queryTemplate",
                "queryLimit" to 1,
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation createTemplateQuery(\$input: CreateTemplateQueryInput!) { createTemplateQuery(input: \$input) { id } }",
                    "data.createTemplateQuery.id",
                    mapOf("input" to createTemplateQueryInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateQueryService, times(0)).create(any())
    }

    @Test
    fun `createTemplateQuery does not call create if not isExpressionTemplateAllowed`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.CREATE_TEMPLATE.name }))

        val createTemplateQueryInput =
            mapOf(
                "templateId" to 10,
                "nameTemplate" to "Some nameTemplate",
                "queryTemplate" to "Some queryTemplate",
                "queryLimit" to 1,
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation createTemplateQuery(\$input: CreateTemplateQueryInput!) { createTemplateQuery(input: \$input) { id } }",
                    "data.createTemplateQuery.id",
                    mapOf("input" to createTemplateQueryInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateQueryService, times(0)).create(any())
    }

    @Test
    fun `updateTemplateQuery calls update if user has UPDATE_TEMPLATE authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.UPDATE_TEMPLATE.name }))

        whenever(templateQueryService.update(anyInt(), any()))
            .thenReturn(TEMPLATE_QUERY_FIXTURE_WITH_ID_1)

        whenever(accessService.isExpressionTemplateAllowed("Some queryTemplate", GroupAuthorisationEnum.CREATE_OR_EDIT_TEMPLATE_QUERY))
            .thenReturn(true)

        val updateTemplateQueryInput =
            mapOf(
                "templateId" to 10,
                "nameTemplate" to "Some nameTemplate",
                "queryTemplate" to "Some queryTemplate",
                "queryLimit" to 1,
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation updateTemplateQuery(\$id: ID!, \$input: UpdateTemplateQueryInput!) { updateTemplateQuery(id: \$id, input: \$input) { id } }",
                "data.updateTemplateQuery.id",
                mapOf("id" to TEMPLATE_QUERY_FIXTURE_WITH_ID_1.id, "input" to updateTemplateQueryInput),
            )

        assertThat(result).isEqualTo(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.id.toString())

        verify(templateQueryService).update(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.id, UPDATE_TEMPLATE_QUERY_FIXTURE_1)
    }

    @Test
    fun `updateTemplateQuery does not call update if user does not have UPDATE_TEMPLATE authorisation`() {
        whenever(accessService.isExpressionTemplateAllowed("Some queryTemplate", GroupAuthorisationEnum.CREATE_OR_EDIT_TEMPLATE_QUERY))
            .thenReturn(true)

        val updateTemplateQueryInput =
            mapOf(
                "templateId" to 10,
                "nameTemplate" to "Some nameTemplate",
                "queryTemplate" to "Some queryTemplate",
                "queryLimit" to 1,
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation updateTemplateQuery(\$id: ID!, \$input: UpdateTemplateQueryInput!) { updateTemplateQuery(id: \$id, input: \$input) { id } }",
                    "data.updateTemplateQuery.id",
                    mapOf("id" to TEMPLATE_QUERY_FIXTURE_WITH_ID_1.id, "input" to updateTemplateQueryInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateQueryService, times(0)).update(anyInt(), any())
    }

    @Test
    fun `updateTemplateQuery does not call update if not isExpressionTemplateAllowed`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.UPDATE_TEMPLATE.name }))

        val updateTemplateQueryInput =
            mapOf(
                "templateId" to 10,
                "nameTemplate" to "Some nameTemplate",
                "queryTemplate" to "Some queryTemplate",
                "queryLimit" to 1,
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation updateTemplateQuery(\$id: ID!, \$input: UpdateTemplateQueryInput!) { updateTemplateQuery(id: \$id, input: \$input) { id } }",
                    "data.updateTemplateQuery.id",
                    mapOf("id" to TEMPLATE_QUERY_FIXTURE_WITH_ID_1.id, "input" to updateTemplateQueryInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateQueryService, times(0)).update(anyInt(), any())
    }

    @Test
    fun `deleteTemplateQuery calls delete if user has DELETE_TEMPLATE authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.DELETE_TEMPLATE.name }))

        whenever(templateQueryService.deleteById(any()))
            .thenReturn(true)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                "mutation deleteTemplateQuery(\$id: ID!) { deleteTemplateQuery(id: \$id) }",
                "data.deleteTemplateQuery",
                mapOf("id" to 1),
            )

        assertThat(result).isEqualTo(true)

        verify(templateQueryService).deleteById(1)
    }

    @Test
    fun `deleteTemplateQuery does not call delete if user does not have DELETE_TEMPLATE authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                    "mutation deleteTemplateQuery(\$id: ID!) { deleteTemplateQuery(id: \$id) }",
                    "data.deleteTemplateQuery",
                    mapOf("id" to 1),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateQueryService, times(0)).deleteById(anyInt())
    }

    @Test
    fun `templateQuerys_template can fetch if user has VIEW_TEMPLATES authority`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_TEMPLATES.name }))

        whenever(templateQueryService.findAll())
            .thenReturn(listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1))

        whenever(templateService.findByIds(setOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.templateId)))
            .thenReturn(listOf(TEMPLATE_FIXTURE_WITH_ID_1.copy(id = TEMPLATE_QUERY_FIXTURE_WITH_ID_1.templateId)))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ templateQuerys { template { id } } }",
                "data.templateQuerys[*].template.id",
            )

        assertThat(ids).contains(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.templateId.toString())
    }

    @Test
    fun `templateQuerys_template cannot fetch if user does not have authority`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ templateQuerys { template { id } } }",
                    "data.templateQuerys[*].template.id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateQueryService, times(0)).findAll()

        verify(templateService, times(0)).findByIds(any())
    }

    @Test
    fun `templates_templateQuerys can fetch if user has VIEW_TEMPLATES authority`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_TEMPLATES.name }))

        whenever(templateService.findAll())
            .thenReturn(listOf(TEMPLATE_FIXTURE_WITH_ID_1.copy(id = TEMPLATE_QUERY_FIXTURE_WITH_ID_1.templateId)))

        whenever(templateQueryService.findByTemplateIds(setOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.templateId)))
            .thenReturn(listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ templates { templateQuerys { id } } }",
                "data.templates[*].templateQuerys[*].id",
            )

        assertThat(ids).contains(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `templates_templateQuerys cannot fetch if user does not have VIEW_TEMPLATES authority`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ templates { templateQuerys { id } } }",
                    "data.templates[*].templateQuerys[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateService, times(0)).findAll()

        verify(templateQueryService, times(0)).findByTemplateIds(any())
    }

    @Test
    fun `templateQuerys_application can fetch if user has VIEW_TEMPLATES authority and VIEW_APPLICATION group authority`() {
        whenever(authenticationMock.authorities)
            .thenReturn(
                listOf(
                    GrantedAuthority { AuthorisationEnum.VIEW_TEMPLATES.name },
                ),
            )

        whenever(
            accessService.isAuthorisedByApplicationIds(
                setOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.applicationId),
                GroupAuthorisationEnum.VIEW_APPLICATION,
            ),
        )
            .thenReturn(true)

        whenever(templateQueryService.findAll())
            .thenReturn(listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1))

        whenever(applicationService.findByIds(setOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.applicationId)))
            .thenReturn(listOf(APPLICATION_FIXTURE_WITH_ID_1.copy(id = TEMPLATE_QUERY_FIXTURE_WITH_ID_1.applicationId)))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ templateQuerys { application { id } } }",
                "data.templateQuerys[*].application.id",
            )

        assertThat(ids).contains(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.applicationId.toString())
    }

    @Test
    fun `templateQuerys_application cannot fetch if user does not have VIEW_TEMPLATES authority and VIEW_APPLICATION group authorities`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ templateQuerys { application { id } } }",
                    "data.templateQuerys[*].application.id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(templateQueryService, times(0)).findAll()

        verify(applicationService, times(0)).findByIds(any())
    }

    @Test
    fun `applications_templateQuerys can fetch if user has VIEW_APPLICATIONS and VIEW_TEMPLATES authority`() {
        whenever(authenticationMock.authorities)
            .thenReturn(
                listOf(
                    GrantedAuthority { AuthorisationEnum.VIEW_TEMPLATES.name },
                    GrantedAuthority { AuthorisationEnum.VIEW_APPLICATIONS.name },
                ),
            )

        whenever(applicationService.findAll())
            .thenReturn(listOf(APPLICATION_FIXTURE_WITH_ID_1.copy(id = TEMPLATE_QUERY_FIXTURE_WITH_ID_1.applicationId)))

        whenever(templateQueryService.findByApplicationIds(setOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.applicationId)))
            .thenReturn(listOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ applications { templateQuerys { id } } }",
                "data.applications[*].templateQuerys[*].id",
            )

        assertThat(ids).contains(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `applications_templateQuerys cannot fetch if user does not have VIEW_APPLICATIONS nor VIEW_TEMPLATES authorities`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ applications { templateQuerys { id } } }",
                    "data.applications[*].templateQuerys[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(applicationService, times(0)).findAll()

        verify(templateQueryService, times(0)).findByApplicationIds(setOf(TEMPLATE_QUERY_FIXTURE_WITH_ID_1.applicationId))
    }
}
