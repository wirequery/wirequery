package com.wirequery.manager.application.graphql

import com.netflix.graphql.types.errors.ErrorType
import com.netflix.graphql.types.errors.TypedGraphQLError
import com.wirequery.manager.domain.FunctionalException
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.ProviderNotFoundException
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture

@Component
class GraphQLExceptionHandler : DataFetcherExceptionHandler {
    override fun handleException(
        handlerParameters: DataFetcherExceptionHandlerParameters,
    ): CompletableFuture<DataFetcherExceptionHandlerResult> {
        val exception = handlerParameters.exception
        exception.printStackTrace()
        val supportedException = findSupportedExceptionsInCause(exception)
        return handleException(supportedException ?: exception, handlerParameters)
    }

    private fun findSupportedExceptionsInCause(e: Throwable): Throwable? {
        if (supportedExceptions.any { it.isAssignableFrom(e::class.java) }) {
            return e
        }
        if (e.cause != null) {
            return findSupportedExceptionsInCause(e.cause as Throwable)
        }
        return e
    }

    private fun handleException(
        e: Throwable,
        handlerParameters: DataFetcherExceptionHandlerParameters,
    ): CompletableFuture<DataFetcherExceptionHandlerResult> {
        return when (e) {
            is ProviderNotFoundException ->
                completedFuture(
                    DataFetcherExceptionHandlerResult.newResult()
                        .error(
                            TypedGraphQLError.newConflictBuilder()
                                .errorType(ErrorType.PERMISSION_DENIED)
                                .message("Permission denied")
                                .path(handlerParameters.path)
                                .build(),
                        )
                        .build(),
                )

            is AuthenticationCredentialsNotFoundException ->
                completedFuture(
                    DataFetcherExceptionHandlerResult.newResult()
                        .error(
                            TypedGraphQLError.newConflictBuilder()
                                .errorType(ErrorType.PERMISSION_DENIED)
                                .message("Permission denied")
                                .path(handlerParameters.path)
                                .build(),
                        )
                        .build(),
                )

            is AccessDeniedException ->
                completedFuture(
                    DataFetcherExceptionHandlerResult.newResult()
                        .error(
                            TypedGraphQLError.newConflictBuilder()
                                .errorType(ErrorType.PERMISSION_DENIED)
                                .message("Permission denied")
                                .path(handlerParameters.path)
                                .build(),
                        )
                        .build(),
                )

            is FunctionalException ->
                completedFuture(
                    DataFetcherExceptionHandlerResult.newResult()
                        .error(
                            TypedGraphQLError.newConflictBuilder()
                                .message(e.message)
                                .path(handlerParameters.path)
                                .build(),
                        )
                        .build(),
                )

            else ->
                completedFuture(
                    DataFetcherExceptionHandlerResult.newResult()
                        .error(
                            TypedGraphQLError.newInternalErrorBuilder()
                                .message("An unknown error occurred")
                                .path(handlerParameters.path)
                                .build(),
                        )
                        .build(),
                ).also { handlerParameters.exception.printStackTrace() }
        }
    }

    private companion object {
        private val supportedExceptions =
            listOf(
                AccessDeniedException::class.java,
                FunctionalException::class.java,
            )
    }
}
