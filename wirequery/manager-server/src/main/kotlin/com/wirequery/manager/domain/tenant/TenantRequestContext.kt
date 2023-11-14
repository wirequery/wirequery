// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.tenant

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import kotlin.properties.Delegates

@Component
@RequestScope
class TenantRequestContext {
    var tenantId by Delegates.notNull<Int>()
}
