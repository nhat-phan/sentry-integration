package net.ntworld.sentryIntegrationIdea.projectTab.queryTab

import net.ntworld.sentryIntegration.entity.SentryEventDetail
import net.ntworld.sentryIntegration.entity.SentryEventException
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryIssueSortedByEnum
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.View
import java.util.*

interface QueryTabView : View<QueryTabView.ActionListener>, Component {

    fun displayIssueDetail(issue: SentryIssue, issueDetail: SentryIssueDetail)

    fun displayEventDetail(issue: SentryIssue, eventDetail: SentryEventDetail)

    fun displayIssueDetailLoadingState(issue: SentryIssue)

    fun displaySingleIssueToolbar(issue: SentryIssue)

    fun hideIssueDetail()

    fun displayIssues(
        issues: List<SentryIssue>,
        eventDetailsMap: Map<String, SentryEventDetail>,
        displayEventCount: Boolean
    )

    fun isIssueNodeExpanded(issue: SentryIssue): Boolean

    fun updateIssueIfDetailIsOpened(issue: SentryIssue)

    fun setTeamsAndUsers(teams: List<SentryTeam>, users: List<SentryUser>)

    interface ActionListener : EventListener {
        fun onQueryChanged(query: String, refreshClicked: Boolean)

        fun onSortedByChanged(sortedBy: SentryIssueSortedByEnum)

        fun onDisplayEventCountChanged(display: Boolean)

        fun onIssuesSelected(issues: List<SentryIssue>)

        fun onStacktraceSelected(
            issue: SentryIssue,
            exception: SentryEventException,
            stacktrace: SentryEventExceptionStacktrace,
            index: Int
        )

        fun onIssueNodeExpanded(issue: SentryIssue)

        fun onIssueNodeCollapsed(issue: SentryIssue)

        fun onSingleIssueActionClicked(issue: SentryIssue, action: SingleIssueAction, params: Any?)
    }

    enum class SingleIssueAction {
        OPEN_IN_BROWSER,
        COPY_LINK,
        RESOLVE,
        UNRESOLVE,
        MARK_AS_SEEN,
        MARK_AS_UNSEEN,
        IGNORE,
        BOOKMARK,
        REMOVE_BOOKMARK,
        SUBSCRIBE,
        UNSUBSCRIBE,
        CHANGE_ASSIGNEE
    }

    enum class MultipleIssuesAction {
        MERGED,
        RESOLVE
    }
}