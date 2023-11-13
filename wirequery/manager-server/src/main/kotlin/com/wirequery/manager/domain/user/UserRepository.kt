package com.wirequery.manager.domain.user

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface UserRepository : CrudRepository<UserEntity, Int> {
    @Query("SELECT * FROM users WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: Iterable<Int>,
    ): List<UserEntity>

    @Query("SELECT * FROM users WHERE username = :username")
    fun findByUsername(
        @Param("username") username: String,
    ): UserEntity?
}
