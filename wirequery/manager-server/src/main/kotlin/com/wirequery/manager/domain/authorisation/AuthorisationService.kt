// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

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
