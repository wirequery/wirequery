package com.wirequery.manager.domain.query

import com.wirequery.manager.domain.global.PubSubService
import com.wirequery.manager.ee.domain.quarantine.QuarantineService
import com.wirequery.manager.ee.domain.quarantinerule.QuarantineRuleService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class QueryReportServiceImpl(
    private val pubSubService: PubSubService,
    private val quarantineService: QuarantineService,
    private val quarantineRuleService: QuarantineRuleService,
    private val publisher: ApplicationEventPublisher,
) : QueryReportService {
    override fun reportQueryResults(queryReports: List<QueryReport>) {
        val quarantineRules = quarantineRuleService.findAllEnabled()
        queryReports.forEach { queryReport ->
            if (!quarantineService.checkIsSafeOrBroadcastQuarantineEvent(queryReport, quarantineRules)) {
                pubSubService.publish(
                    "query_removals:${queryReport.appName}",
                    QueryService.QueryMutation.RemoveQueryMutationById(queryReport.queryId),
                )
            } else {
                publisher.publishEvent(QueryEvent.QueryReportedEvent(this, queryReport))
                if (!queryReport.queryId.endsWith(":trace")) {
                    pubSubService.publish("query_report:${queryReport.queryId}", queryReport)
                }
            }
        }
    }
}
