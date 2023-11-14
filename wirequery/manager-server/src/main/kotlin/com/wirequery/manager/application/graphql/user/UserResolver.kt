// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.application.graphql.user

import com.netflix.graphql.dgs.*
import com.wirequery.manager.domain.user.User
import com.wirequery.manager.domain.user.UserService
import com.wirequery.manager.domain.user.UserService.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import java.util.concurrent.CompletableFuture

@DgsComponent
class UserResolver(
    private val userService: UserService,
    private val authenticationProvider: AuthenticationProvider,
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun currentUser(): User? {
        return userService.findCurrentUser()
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun user(id: Int): User? {
        return userService.findById(id)
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun users(): Iterable<User> {
        return userService.findAll()
    }

    @DgsMutation
    fun login(input: LoginInput): User? {
        return try {
            val credentials = UsernamePasswordAuthenticationToken(input.username, input.password)
            SecurityContextHolder.getContext().authentication = authenticationProvider.authenticate(credentials)
            currentUser()
        } catch (e: AuthenticationException) {
            null
        }
    }

    @DgsMutation
    fun logout(): Boolean {
        SecurityContextHolder.getContext().authentication = null
        return true
    }

    @DgsMutation
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).MANAGE_USERS.name())")
    fun register(input: RegisterInput): User {
        return userService.register(input)
    }

    @DgsMutation
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).MANAGE_USERS.name())")
    fun updateUser(
        id: Int,
        input: UpdateUserInput,
    ): User? {
        return userService.update(id, input)
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun updateCurrentUser(input: UpdateCurrentUserInput): User? {
        return userService.updateCurrentUser(input)
    }

    @DgsMutation
    @PreAuthorize("hasAuthority(T(com.wirequery.manager.domain.authorisation.AuthorisationEnum).MANAGE_USERS.name())")
    fun deleteUser(id: Int): Boolean {
        return userService.deleteById(id)
    }

    @DgsData(parentType = "User", field = "authorisationNames")
    fun authorisationNames(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<String>> {
        val user = dfe.getSource<User>()
        return dfe.getDataLoader<User, List<String>>("authorisationsByUser")
            .load(user)
    }
}
