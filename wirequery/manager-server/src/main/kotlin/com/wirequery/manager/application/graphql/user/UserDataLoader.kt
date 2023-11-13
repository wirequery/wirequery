package com.wirequery.manager.application.graphql.user

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataLoader
import com.wirequery.manager.domain.user.User
import com.wirequery.manager.domain.user.UserService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executor

@DgsComponent
class UserDataLoader(
    private val userService: UserService,
    private val executor: Executor,
) {
    @DgsDataLoader(name = "userById")
    val userById =
        MappedBatchLoader<Int, User?> {
            supplyAsync({
                userService.findByIds(it).associateBy { it.id }
            }, executor)
        }

    @DgsDataLoader(name = "authorisationsByUser")
    val authorisationsByUser =
        MappedBatchLoader<User, List<String>> {
            supplyAsync({
                userService.findAuthorisationNamesByUsers(it)
            }, executor)
        }
}
