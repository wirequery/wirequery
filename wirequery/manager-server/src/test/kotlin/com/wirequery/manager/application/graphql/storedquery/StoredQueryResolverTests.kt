package com.wirequery.manager.application.graphql.storedquery

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.exceptions.QueryException
import com.wirequery.manager.application.ResolverTestContext
import com.wirequery.manager.application.graphql.GraphQLExceptionHandler
import com.wirequery.manager.application.graphql.application.ApplicationByIdDataLoader
import com.wirequery.manager.application.graphql.application.ApplicationResolver
import com.wirequery.manager.domain.access.AccessService
import com.wirequery.manager.domain.application.ApplicationFixtures.APPLICATION_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.application.ApplicationService
import com.wirequery.manager.domain.authorisation.AuthorisationEnum
import com.wirequery.manager.domain.groupauthorisation.GroupAuthorisationEnum
import com.wirequery.manager.domain.storedquery.StoredQueryFixtures.STORED_QUERY_FIXTURE_WITH_ID_1
import com.wirequery.manager.domain.storedquery.StoredQueryService
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
        StoredQueryResolver::class,
        StoredQueryByIdDataLoader::class,
        StoredQuerysByApplicationIdDataLoader::class,
        StoredQuerysBySessionIdDataLoader::class,
        GraphQLExceptionHandler::class,
        AccessService::class,
        GraphQLExceptionHandler::class,
    ],
)
class StoredQueryResolverTests : ResolverTestContext() {
    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockBean
    private lateinit var accessService: AccessService

    @MockBean
    private lateinit var applicationService: ApplicationService

    @MockBean
    private lateinit var storedQueryService: StoredQueryService

    // TODO stored querys singular

    @Test
    fun `storedQuerys fetches stored queries if the user has VIEW_STORED_QUERIES authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_STORED_QUERIES.name }))

        whenever(storedQueryService.findAll())
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ storedQuerys { id } }",
                "data.storedQuerys[*].id",
            )

        assertThat(ids).contains(STORED_QUERY_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `storedQuerys does not fetch stored queries if the user does not have VIEW_STORED_QUERIES authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ storedQuerys { id } }",
                    "data.storedQuerys[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(storedQueryService, times(0)).findAll()
    }

    @Test
    fun `createStoredQuery throws exception if creating is not allowed`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.CREATE_STORED_QUERY.name }))

        whenever(accessService.isExpressionAllowed(STORED_QUERY_FIXTURE_WITH_ID_1.query, GroupAuthorisationEnum.STORE_QUERY))
            .thenReturn(false)

        val createStoredQueryInput =
            mapOf(
                "name" to "Some name",
                "type" to "TAPPING",
                "query" to "Some query",
                "queryLimit" to 1,
                "endDate" to "2100-01-01T00:00:00Z",
            )

        val exception =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation createStoredQuery(\$input: CreateStoredQueryInput!) { createStoredQuery(input: \$input) { id } }",
                    "data.createStoredQuery.id",
                    mapOf("input" to createStoredQueryInput),
                )
            }

        assertThat(exception.errors[0].message)
            .isEqualTo("Application does not exist or you're not authorized to create a query for it.")

        verify(storedQueryService, times(0))
            .create(any<StoredQueryService.CreateStoredQueryInput>())
    }

    @Test
    fun `createStoredQuery calls create if user has CREATE_STORED_QUERY authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.CREATE_STORED_QUERY.name }))

        whenever(accessService.isExpressionAllowed(any(), any()))
            .thenReturn(true)

        whenever(storedQueryService.create(any()))
            .thenReturn(STORED_QUERY_FIXTURE_WITH_ID_1)

        val createStoredQueryInput =
            mapOf(
                "name" to "Some name",
                "type" to "TAPPING",
                "query" to "Some query",
                "queryLimit" to 1,
                "endDate" to "2100-01-01T00:00:00Z",
            )

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<String>(
                "mutation createStoredQuery(\$input: CreateStoredQueryInput!) { createStoredQuery(input: \$input) { id } }",
                "data.createStoredQuery.id",
                mapOf("input" to createStoredQueryInput),
            )

        assertThat(result).isEqualTo(STORED_QUERY_FIXTURE_WITH_ID_1.id.toString())

        verify(accessService).isExpressionAllowed("Some query", GroupAuthorisationEnum.STORE_QUERY)

        verify(storedQueryService).create(any<StoredQueryService.CreateStoredQueryInput>())
    }

    @Test
    fun `createStoredQuery does not call create if user does not have CREATE_STORED_QUERY authorisation`() {
        val createStoredQueryInput =
            mapOf(
                "name" to "Some name",
                "type" to "TAPPING",
                "query" to "Some query",
                "queryLimit" to 1,
                "endDate" to "2100-01-01T00:00:00Z",
            )

        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<String>(
                    "mutation createStoredQuery(\$input: CreateStoredQueryInput!) { createStoredQuery(input: \$input) { id } }",
                    "data.createStoredQuery.id",
                    mapOf("input" to createStoredQueryInput),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(accessService, times(0)).isExpressionAllowed(any(), any())

        verify(storedQueryService, times(0)).create(any())
    }

    @Test
    fun `deleteStoredQuery does not delete when user does not have DELETE_STORED_QUERY authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                    "mutation deleteStoredQuery(\$id: ID!) { deleteStoredQuery(id: \$id) }",
                    "data.deleteStoredQuery",
                    mapOf("id" to 1),
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(storedQueryService, times(0)).deleteById(anyInt())
    }

    @Test
    fun `deleteStoredQuery calls delete when user has DELETE_STORED_QUERY authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.DELETE_STORED_QUERY.name }))

        whenever(storedQueryService.deleteById(any()))
            .thenReturn(true)

        val result =
            dgsQueryExecutor.executeAndExtractJsonPath<Boolean>(
                "mutation deleteStoredQuery(\$id: ID!) { deleteStoredQuery(id: \$id) }",
                "data.deleteStoredQuery",
                mapOf("id" to 1),
            )

        assertThat(result).isEqualTo(true)

        verify(storedQueryService).deleteById(1)
    }

    @Test
    fun `storedQuerys_application fetches application if user has VIEW_STORED_QUERIES authorisation and VIEW_APPLICATION group authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_STORED_QUERIES.name }))

        whenever(
            accessService.isAuthorisedByApplicationIds(
                setOf(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId),
                GroupAuthorisationEnum.VIEW_APPLICATION,
            ),
        )
            .thenReturn(true)

        whenever(storedQueryService.findAll())
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1))

        whenever(applicationService.findByIds(setOf(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId)))
            .thenReturn(listOf(APPLICATION_FIXTURE_WITH_ID_1.copy(id = STORED_QUERY_FIXTURE_WITH_ID_1.applicationId)))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ storedQuerys { application { id } } }",
                "data.storedQuerys[*].application.id",
            )

        assertThat(ids).contains(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId.toString())
    }

    @Test
    fun `storedQuerys_application does not fetch application if user does not have VIEW_STORED_QUERIES authorisation nor VIEW_APPLICATION group authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ storedQuerys { application { id } } }",
                    "data.storedQuerys[*].application.id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(storedQueryService, times(0)).findAll()

        verify(applicationService, times(0)).findByIds(any())
    }

    @Test
    fun `applications_storedQuerys fetches stored queries if user has VIEW_APPLICATIONS authorisation`() {
        whenever(authenticationMock.authorities)
            .thenReturn(listOf(GrantedAuthority { AuthorisationEnum.VIEW_APPLICATIONS.name }))

        whenever(
            accessService.isAuthorisedByApplicationIds(
                setOf(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId),
                GroupAuthorisationEnum.VIEW_STORED_QUERY,
            ),
        )
            .thenReturn(true)

        whenever(applicationService.findAll())
            .thenReturn(listOf(APPLICATION_FIXTURE_WITH_ID_1.copy(id = STORED_QUERY_FIXTURE_WITH_ID_1.applicationId)))

        whenever(storedQueryService.findByApplicationIds(setOf(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId)))
            .thenReturn(listOf(STORED_QUERY_FIXTURE_WITH_ID_1))

        val ids =
            dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                "{ applications { storedQuerys { id } } }",
                "data.applications[*].storedQuerys[*].id",
            )

        assertThat(ids).contains(STORED_QUERY_FIXTURE_WITH_ID_1.id.toString())
    }

    @Test
    fun `applications_storedQuerys does not fetch stored queries if user does not have VIEW_APPLICATIONS authorisation`() {
        val queryException =
            assertThrows<QueryException> {
                dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
                    "{ applications { storedQuerys { id } } }",
                    "data.applications[*].storedQuerys[*].id",
                )
            }

        assertThat(queryException.errors[0].extensions["errorType"])
            .isEqualTo("PERMISSION_DENIED")

        verify(applicationService, times(0))
            .findAll()

        verify(storedQueryService, times(0))
            .findByApplicationIds(setOf(STORED_QUERY_FIXTURE_WITH_ID_1.applicationId))
    }
}
