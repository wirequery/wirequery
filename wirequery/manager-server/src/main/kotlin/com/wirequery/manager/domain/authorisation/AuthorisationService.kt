package com.wirequery.manager.domain.authorisation

import org.springframework.stereotype.Service

@Service
class AuthorisationService {
    fun findByNames(names: Collection<String>): List<Authorisation> {
        val namesSet = names.toSet()
        return findAll().filter { it.name in namesSet }
    }

    fun findAll(): List<Authorisation> {
        return AuthorisationEnum.entries.map {
            Authorisation(
                name = it.name,
                label = it.label,
                description = it.description,
            )
        }
    }
}
