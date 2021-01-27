package net.ntworld.sentryIntegrationIdea.projectTab.queryTab

import net.ntworld.sentryIntegration.entity.IssueQuery
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryEventDetail
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryIssueSortedByEnum
import net.ntworld.sentryIntegrationIdea.Model
import java.util.*

interface QueryTabModel: Model<QueryTabModel.DataListener> {
    val name: String

    val linkedProject: LinkedProject

    val initialQuery: IssueQuery

    var query: String

    var sortedBy: SentryIssueSortedByEnum

    var displayEventCount: Boolean

    val issues: List<SentryIssue>

    val eventDetailsMap: Map<String, SentryEventDetail>

    val issueDetailsMap: Map<String, SentryIssueDetail>

    val selectedIssueIds: List<String>

    fun setIssues(issues: List<SentryIssue>)

    fun setIssueDetail(issueId: String, issueDetail: SentryIssueDetail)

    fun setEventDetail(issueId: String, eventDetail: SentryEventDetail)

    fun setSelectedIssueIds(issueIds: List<String>)

    fun isIssueDetailLoaded(issue: SentryIssue): Boolean

    fun markIssueDetailLoadStatus(issue: SentryIssue, loaded: Boolean)

    fun clearIssueDetailData()

    fun updateSingleIssueState(issue: SentryIssue)

    fun collectSelectedIssues(): List<SentryIssue>

    interface DataListener : EventListener {
        fun whenSingleIssueStateUpdated(issue: SentryIssue)

        fun whenQueryChanged(query: String)

        fun whenSortedByChanged(sortedBy: SentryIssueSortedByEnum)

        fun whenDisplayEventCountChanged(display: Boolean)

        fun whenEventDetailsMapChanged(eventDetailsMap: Map<String, SentryEventDetail>)

        fun whenSelectedIssueIdsChanged(issueIds: List<String>)
    }
}