package net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component

import net.ntworld.sentryIntegration.entity.SentryEventException
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.node.issue.IssuesTreeData
import java.util.*

interface IssuesTreeComponent : Component {
    fun addActionListener(listener: Listener)

    fun removeActionListener(listener: Listener)

    fun setData(data: IssuesTreeData)

    fun isIssueNodeExpanded(issue: SentryIssue): Boolean

    interface Listener : EventListener {
        fun onIssuesSelected(issues: List<SentryIssue>)

        fun onStacktraceSelected(
            issue: SentryIssue,
            exception: SentryEventException,
            stacktrace: SentryEventExceptionStacktrace,
            index: Int
        )

        fun onIssueNodeExpanded(issue: SentryIssue)

        fun onIssueNodeCollapsed(issue: SentryIssue)
    }
}