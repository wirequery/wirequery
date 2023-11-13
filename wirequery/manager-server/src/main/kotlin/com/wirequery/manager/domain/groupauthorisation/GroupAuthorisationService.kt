package com.wirequery.manager.domain.groupauthorisation

import org.springframework.stereotype.Service

@Service
class GroupAuthorisationService {
    fun findByNames(names: Collection<String>): List<GroupAuthorisation> {
        val namesSet = names.toSet()
        return findAll().filter { it.name in namesSet }
    }

    fun findAll(): List<GroupAuthorisation> {
        return GroupAuthorisationEnum.entries.map {
            GroupAuthorisation(name = it.name, label = it.label, description = it.description)
        }
    }
}
