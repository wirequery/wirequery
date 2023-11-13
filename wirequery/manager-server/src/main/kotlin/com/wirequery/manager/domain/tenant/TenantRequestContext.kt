package com.wirequery.manager.domain.tenant

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import kotlin.properties.Delegates

@Component
@RequestScope
class TenantRequestContext {
    var tenantId by Delegates.notNull<Int>()
}
