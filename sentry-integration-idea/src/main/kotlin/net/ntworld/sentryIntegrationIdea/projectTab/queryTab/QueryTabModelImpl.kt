package net.ntworld.sentryIntegrationIdea.projectTab.queryTab

import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.IssueQuery
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryEventDetail
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryIssueSortedByEnum
import net.ntworld.sentryIntegrationIdea.AbstractModel

class QueryTabModelImpl(
    override val linkedProject: LinkedProject,
    override val name: String,
    override val initialQuery: IssueQuery
) : AbstractModel<QueryTabModel.DataListener>(), QueryTabModel {
    override val dispatcher = EventDispatcher.create(QueryTabModel.DataListener::class.java)
    private val loadedIssuesMap = mutableMapOf<String, Boolean>()
    private val myIssuesMap = mutableMapOf<String, SentryIssue>()

    override var query: String = initialQuery.toQueryString()
        set(value) {
            val text = value.trim()
            if (text != field) {
                field = text
                dispatcher.multicaster.whenQueryChanged(text)
            }
        }

    override var sortedBy: SentryIssueSortedByEnum = SentryIssueSortedByEnum.LastSeen
        set(value) {
            if (value != field) {
                field = value
                dispatcher.multicaster.whenSortedByChanged(field)
            }
        }

    override var displayEventCount: Boolean = true
        set(value) {
            if (value != field) {
                field = value
                dispatcher.multicaster.whenDisplayEventCountChanged(value)
            }
        }

    override val issues
        get() = myIssuesMap.values.toList()

    override val eventDetailsMap = mutableMapOf<String, SentryEventDetail>()

    override val issueDetailsMap = mutableMapOf<String, SentryIssueDetail>()

    override val selectedIssueIds = mutableListOf<String>()

    override fun setIssues(issues: List<SentryIssue>) {
        this.myIssuesMap.clear()
        for (issue in issues) {
            this.myIssuesMap[issue.id] = issue
        }

        dispatcher.multicaster.whenSelectedIssueIdsChanged(this.selectedIssueIds)
    }

    override fun setEventDetail(issueId: String, eventDetail: SentryEventDetail) {
        eventDetailsMap[issueId] = eventDetail

        dispatcher.multicaster.whenEventDetailsMapChanged(this.eventDetailsMap)
    }

    override fun setIssueDetail(issueId: String, issueDetail: SentryIssueDetail) {
        issueDetailsMap[issueId] = issueDetail
    }

    override fun setSelectedIssueIds(issueIds: List<String>) {
        if (isSelectedIssueIdsChange(issueIds)) {
            this.selectedIssueIds.clear()
            this.selectedIssueIds.addAll(issueIds)

            dispatcher.multicaster.whenSelectedIssueIdsChanged(this.selectedIssueIds)
        }
    }

    override fun isIssueDetailLoaded(issue: SentryIssue): Boolean {
        val data = loadedIssuesMap[issue.id]
        return null !== data && data
    }

    override fun markIssueDetailLoadStatus(issue: SentryIssue, loaded: Boolean) {
        loadedIssuesMap[issue.id] = loaded
    }

    override fun clearIssueDetailData() {
        loadedIssuesMap.clear()
        issueDetailsMap.clear()
        eventDetailsMap.clear()
    }

    override fun updateSingleIssueState(issue: SentryIssue) {
        this.myIssuesMap[issue.id] = issue

        dispatcher.multicaster.whenSingleIssueStateUpdated(issue)
    }

    override fun collectSelectedIssues(): List<SentryIssue> {
        val issues = mutableListOf<SentryIssue>()
        for (selectedId in selectedIssueIds) {
            val issue = myIssuesMap[selectedId]
            if (null !== issue) {
                issues.add(issue)
            }
        }
        return issues
    }

    private fun isSelectedIssueIdsChange(issueIds: List<String>): Boolean {
        if (issueIds.count() != this.selectedIssueIds.count()) {
            return true
        }
        for (item in issueIds) {
            if (!selectedIssueIds.contains(item)) {
                return true
            }
        }
        return false
    }
}