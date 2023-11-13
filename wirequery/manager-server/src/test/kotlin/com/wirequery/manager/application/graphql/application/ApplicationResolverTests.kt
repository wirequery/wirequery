package com.wirequery.manager.application.graphql.application

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.domain.access.AccessService
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.application.ApplicationFixtures.UPDATE_APPLICATION_FIXTURE_1
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.application.ApplicationService.UnquarantineApplicationInput
import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum.*
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
        ApplicationResolver::class,
        ApplicationByIdDataLoader::class,
        AccessService::class,
        GraphQLExceptionHandler::class,
    ],
)
class ApplicationResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var applicationService: ApplicationService

    @MockBean
    private lateinit var accessService: AccessService

    @Test
    fun `application can be fetched if user has VIEW_APPLICATIONS authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_APPLICATIONS.name }))

        whenever(applicationService.findById(APPLICATION_FIXTURE_WITH_ID_1.id))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1)

        val id =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "query application(\$id: ID!) { application(id: \$id) { id } }",
                "data.application.id",
                mapOf("id" to "" + APPLICATION_FIXTURE_WITH_ID_1.id),
            )

        assertThat(id).isEqualTo(APPLICATION_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `application can be fetched if user has VIEW_APPLICATION group authorisation`() {
        whenever(
            accessService.isAuthorisedByApplicationId(
                APPLICATION_FIXTURE_WITH_ID_1.id,
                VIEW_APPLICATION,
            ),
        )
            .thenReturn(true)

        whenever(applicationService.findById(APPLICATION_FIXTURE_WITH_ID_1.id))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1)

        val id =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "query application(\$id: ID!) { application(id: \$id) { id } }",
                "data.application.id",
                mapOf("id" to "" + APPLICATION_FIXTURE_WITH_ID_1.id),
            )

        assertThat(id).isEqualTo(APPLICATION_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `application cannot be fetched if user is unauthorized`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "query application(\$id: ID!) { application(id: \$id) { id } }",
                    "data.application.id",
                    mapOf("id" to "" + APPLICATION_FIXTURE_WITH_ID_1.id),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(applicationService, times(0))
            .findById(APPLICATION_FIXTURE_WITH_ID_1.id)
    }

    @Test
    fun `revealApiKey can be fetched if user has VIEW_API_KEY group authorisation`() {
        whenever(
            accessService.isAuthorisedByApplicationId(
                APPLICATION_FIXTURE_WITH_ID_1.id,
                VIEW_API_KEY,
            ),
        )
            .thenReturn(true)

        whenever(applicationService.findApiKeyById(APPLICATION_FIXTURE_WITH_ID_1.id))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1.apiKey)

        val apiKey =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation revealApiKey(\$id: ID!) { revealApiKey(id: \$id) }",
                "data.revealApiKey",
                mapOf("id" to "" + APPLICATION_FIXTURE_WITH_ID_1.id),
            )

        assertThat(apiKey).isEqualTo(APPLICATION_FIXTURE_WITH_ID_1.apiKey)
    }

    @Test
    fun `revealApiKey cannot be fetched if user does not have VIEW_API_KEY group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation revealApiKey(\$id: ID!) { revealApiKey(id: \$id) }",
                    "data.revealApiKey",
                    mapOf("id" to "" + APPLICATION_FIXTURE_WITH_ID_1.id),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(applicationService, times(0)).findById(anyInt())
    }

    @Test
    fun `applications can be fetched if user has VIEW_APPLICATIONS authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_APPLICATIONS.name }))

        whenever(applicationService.findAll())
            .thenReturn(listOf(APPLICATION_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ applications { id } }",
                "data.applications[*].id",
            )

        assertThat(ids).contains(APPLICATION_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `applications cannot be fetched if user has VIEW_APPLICATIONS group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ applications { id } }",
                    "data.applications[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(applicationService, times(0)).findAll()
    }

    @Test
    fun `updateApplication calls update if user has UPDATE_APPLICATION group authorisation`() {
        whenever(
            accessService.isAuthorisedByApplicationId(
                APPLICATION_FIXTURE_WITH_ID_1.id,
                UPDATE_APPLICATION,
            ),
        )
            .thenReturn(true)

        whenever(applicationService.update(anyInt(), any()))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1)

        val updateApplicationInput =
            mapOf(
                "description" to "Some description",
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation updateApplication(\$id: ID!, \$input: UpdateApplicationInput!) { updateApplication(id: \$id, input: \$input) { id } }",
                "data.updateApplication.id",
                mapOf("id" to APPLICATION_FIXTURE_WITH_ID_1.id, "input" to updateApplicationInput),
            )

        assertThat(result).isEqualTo(APPLICATION_FIXTURE_WITH_ID_1.id.toString())

        verify(applicationService).update(APPLICATION_FIXTURE_WITH_ID_1.id, UPDATE_APPLICATION_FIXTURE_1)
    }

    @Test
    fun `updateApplication does not call update if user does not have UPDATE_APPLICATION group authorisation`() {
        val updateApplicationInput =
            mapOf(
                "description" to "Some description",
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation updateApplication(\$id: ID!, \$input: UpdateApplicationInput!) { updateApplication(id: \$id, input: \$input) { id } }",
                    "data.updateApplication.id",
                    mapOf("id" to APPLICATION_FIXTURE_WITH_ID_1.id, "input" to updateApplicationInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(applicationService, times(0)).update(anyInt(), any())
    }

    @Test
    fun `unquarantineApplication calls unquarantine if user has UNQUARANTINE_APPLICATION group authorisation`() {
        whenever(
            accessService.isAuthorisedByApplicationId(
                APPLICATION_FIXTURE_WITH_ID_1.id,
                UNQUARANTINE_APPLICATION,
            ),
        )
            .thenReturn(true)

        whenever(applicationService.unquarantine(any(), any()))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation unquarantineApplication(\$id: ID!, \$input: UnquarantineApplicationInput!) { unquarantineApplication(id: \$id, input: \$input) { id } }",
                "data.unquarantineApplication.id",
                mapOf(
                    "id" to APPLICATION_FIXTURE_WITH_ID_1.id,
                    "input" to mapOf("reason" to "reason"),
                ),
            )

        assertThat(result).isEqualTo("" + APPLICATION_FIXTURE_WITH_ID_1.id)

        verify(applicationService).unquarantine(1, UnquarantineApplicationInput(reason = "reason"))
    }

    @Test
    fun `unquarantineApplication calls unquarantine if user has UNQUARANTINE_APPLICATIONS authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.UNQUARANTINE_APPLICATIONS.name }))

        whenever(applicationService.unquarantine(any(), any()))
            .thenReturn(APPLICATION_FIXTURE_WITH_ID_1)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation unquarantineApplication(\$id: ID!, \$input: UnquarantineApplicationInput!) { unquarantineApplication(id: \$id, input: \$input) { id } }",
                "data.unquarantineApplication.id",
                mapOf(
                    "id" to APPLICATION_FIXTURE_WITH_ID_1.id,
                    "input" to mapOf("reason" to "reason"),
                ),
            )

        assertThat(result).isEqualTo("" + APPLICATION_FIXTURE_WITH_ID_1.id)

        verify(applicationService).unquarantine(1, UnquarantineApplicationInput(reason = "reason"))
    }

    @Test
    fun `unquarantineApplication does not call unquarantine if user has UNQUARANTINE_APPLICATION group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation unquarantineApplication(\$id: ID!, \$input: UnquarantineApplicationInput!) { unquarantineApplication(id: \$id, input: \$input) { id } }",
                    "data.unquarantineApplication.id",
                    mapOf(
                        "id" to APPLICATION_FIXTURE_WITH_ID_1.id,
                        "input" to mapOf("reason" to "reason"),
                    ),
                )
            }

        verify(applicationService, times(0)).unquarantine(anyInt(), any())

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")
    }

    @Test
    fun `deleteApplication calls deleteById if user has DELETE_APPLICATION authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.DELETE_APPLICATION.name }))

        whenever(applicationService.deleteById(any()))
            .thenReturn(true)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                "mutation deleteApplication(\$id: ID!) { deleteApplication(id: \$id) }",
                "data.deleteApplication",
                mapOf("id" to 1),
            )

        assertThat(result).isEqualTo(true)

        verify(applicationService).deleteById(1)
    }

    @Test
    fun `deleteApplication calls deleteById if user has DELETE_APPLICATION group authorisation`() {
        whenever(
            accessService.isAuthorisedByApplicationId(
                APPLICATION_FIXTURE_WITH_ID_1.id,
                DELETE_APPLICATION,
            ),
        )
            .thenReturn(true)

        whenever(applicationService.deleteById(any()))
            .thenReturn(true)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                "mutation deleteApplication(\$id: ID!) { deleteApplication(id: \$id) }",
                "data.deleteApplication",
                mapOf("id" to 1),
            )

        assertThat(result).isEqualTo(true)

        verify(applicationService).deleteById(1)
    }

    @Test
    fun `deleteApplication does not call deleteById if user does not have DELETE_APPLICATION group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                    "mutation deleteApplication(\$id: ID!) { deleteApplication(id: \$id) }",
                    "data.deleteApplication",
                    mapOf("id" to 1),
                )
            }

        verify(applicationService, times(0)).deleteById(anyInt())

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")
    }
}
