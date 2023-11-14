// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain

/**
 * A functional exception is an expected exception that is caused by api interaction, instead of
 * technical issues. For example, a validation exception is a form of a Functional Exception.
 *
 * Functional Exception messages are designed to inform the user.
 */
class FunctionalException(message: String, e: Throwable? = null) : RuntimeException(message, e) {
    companion object {
        fun checkFunctional(
            value: Boolean,
            lazyMessage: () -> Any,
        ) {
            if (!value) {
                val message = lazyMessage()
                throw FunctionalException(message.toString())
            }
        }

        fun <T> functionalRequireNotNull(
            message: String,
            value: T?,
        ): T = value ?: functionalError(message)

        fun functionalError(message: String): Nothing = throw FunctionalException(message)
    }
}
