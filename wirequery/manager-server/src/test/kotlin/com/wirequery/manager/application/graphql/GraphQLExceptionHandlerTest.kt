// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql

import com.wirequery.manager.domain.FunctionalException
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.ExecutionStepInfo
import graphql.execution.ResultPath
import graphql.schema.DataFetchingEnvironment
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.ProviderNotFoundException

@ExtendWith(MockitoExtension::class)
class GraphQLExceptionHandlerTest {
    @InjectMocks
    private lateinit var graphQLExceptionHandler: GraphQLExceptionHandler

    @Test
    fun `handleException handles ProviderNotFoundException with Permission Denied`() {
        val mockDataFetchingEnvironment = mock<DataFetchingEnvironment>()
        val mockResultPath = mock<ResultPath>()

        whenever(mockDataFetchingEnvironment.executionStepInfo)
            .thenReturn(
                ExecutionStepInfo.newExecutionStepInfo()
                    .type(mock())
                    .path(mockResultPath).build(),
            )

        val params =
            DataFetcherExceptionHandlerParameters.newExceptionParameters()
                .exception(ProviderNotFoundException("some reason"))
                .dataFetchingEnvironment(mockDataFetchingEnvironment)
                .build()

        val actual = graphQLExceptionHandler.handleException(params)

        assertThat(actual.get().errors[0].message).isEqualTo("Permission denied")
        assertThat(actual.get().errors[0].extensions["errorType"]).isEqualTo("PERMISSION_DENIED")
    }

    @Test
    fun `handleException handles AuthenticationCredentialsNotFoundException with Permission Denied`() {
        val mockDataFetchingEnvironment = mock<DataFetchingEnvironment>()
        val mockResultPath = mock<ResultPath>()

        whenever(mockDataFetchingEnvironment.executionStepInfo)
            .thenReturn(
                ExecutionStepInfo.newExecutionStepInfo()
                    .type(mock())
                    .path(mockResultPath).build(),
            )

        val params =
            DataFetcherExceptionHandlerParameters.newExceptionParameters()
                .exception(AuthenticationCredentialsNotFoundException("some reason"))
                .dataFetchingEnvironment(mockDataFetchingEnvironment)
                .build()

        val actual = graphQLExceptionHandler.handleException(params)

        assertThat(actual.get().errors[0].message).isEqualTo("Permission denied")
        assertThat(actual.get().errors[0].extensions["errorType"]).isEqualTo("PERMISSION_DENIED")
    }

    @Test
    fun `handleException handles AccessDeniedException with Permission Denied`() {
        val mockDataFetchingEnvironment = mock<DataFetchingEnvironment>()
        val mockResultPath = mock<ResultPath>()

        whenever(mockDataFetchingEnvironment.executionStepInfo)
            .thenReturn(
                ExecutionStepInfo.newExecutionStepInfo()
                    .type(mock())
                    .path(mockResultPath).build(),
            )

        val params =
            DataFetcherExceptionHandlerParameters.newExceptionParameters()
                .exception(AccessDeniedException("some reason"))
                .dataFetchingEnvironment(mockDataFetchingEnvironment)
                .build()

        val actual = graphQLExceptionHandler.handleException(params)

        assertThat(actual.get().errors[0].message).isEqualTo("Permission denied")
        assertThat(actual.get().errors[0].extensions["errorType"]).isEqualTo("PERMISSION_DENIED")
    }

    @Test
    fun `handleException handles AccessDeniedException with Permission Denied even if nested`() {
        val mockDataFetchingEnvironment = mock<DataFetchingEnvironment>()
        val mockResultPath = mock<ResultPath>()

        whenever(mockDataFetchingEnvironment.executionStepInfo)
            .thenReturn(
                ExecutionStepInfo.newExecutionStepInfo()
                    .type(mock())
                    .path(mockResultPath).build(),
            )

        val params =
            DataFetcherExceptionHandlerParameters.newExceptionParameters()
                .exception(IllegalStateException(RuntimeException(AccessDeniedException("some reason"))))
                .dataFetchingEnvironment(mockDataFetchingEnvironment)
                .build()

        val actual = graphQLExceptionHandler.handleException(params)

        assertThat(actual.get().errors[0].message).isEqualTo("Permission denied")
        assertThat(actual.get().errors[0].extensions["errorType"]).isEqualTo("PERMISSION_DENIED")
    }

    @Test
    fun `handleException handles FunctionalException with Exception Message`() {
        val mockDataFetchingEnvironment = mock<DataFetchingEnvironment>()
        val mockResultPath = mock<ResultPath>()

        whenever(mockDataFetchingEnvironment.executionStepInfo)
            .thenReturn(
                ExecutionStepInfo.newExecutionStepInfo()
                    .type(mock())
                    .path(mockResultPath).build(),
            )

        val params =
            DataFetcherExceptionHandlerParameters.newExceptionParameters()
                .exception(FunctionalException("some reason"))
                .dataFetchingEnvironment(mockDataFetchingEnvironment)
                .build()

        val actual = graphQLExceptionHandler.handleException(params)

        assertThat(actual.get().errors[0].message).isEqualTo("some reason")
    }

    @Test
    fun `handleException handles FunctionalException even if nested`() {
        val mockDataFetchingEnvironment = mock<DataFetchingEnvironment>()
        val mockResultPath = mock<ResultPath>()

        whenever(mockDataFetchingEnvironment.executionStepInfo)
            .thenReturn(
                ExecutionStepInfo.newExecutionStepInfo()
                    .type(mock())
                    .path(mockResultPath).build(),
            )

        val params =
            DataFetcherExceptionHandlerParameters.newExceptionParameters()
                .exception(IllegalStateException(RuntimeException(FunctionalException("some reason"))))
                .dataFetchingEnvironment(mockDataFetchingEnvironment)
                .build()

        val actual = graphQLExceptionHandler.handleException(params)

        assertThat(actual.get().errors[0].message).isEqualTo("some reason")
    }

    @Test
    fun `handleException handles unsupported exceptions with a generic error message`() {
        val mockDataFetchingEnvironment = mock<DataFetchingEnvironment>()
        val mockResultPath = mock<ResultPath>()

        whenever(mockDataFetchingEnvironment.executionStepInfo)
            .thenReturn(
                ExecutionStepInfo.newExecutionStepInfo()
                    .type(mock())
                    .path(mockResultPath).build(),
            )

        val params =
            DataFetcherExceptionHandlerParameters.newExceptionParameters()
                .exception(IllegalStateException("some reason"))
                .dataFetchingEnvironment(mockDataFetchingEnvironment)
                .build()

        val actual = graphQLExceptionHandler.handleException(params)

        assertThat(actual.get().errors[0].message).isEqualTo("An unknown error occurred")
    }
}
