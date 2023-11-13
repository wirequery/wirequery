package com.wirequery.manager.domain.user

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CurrentUserService {
    fun findCurrentUsername(): String? {
        return SecurityContextHolder.getContext().authentication?.name
    }
}
