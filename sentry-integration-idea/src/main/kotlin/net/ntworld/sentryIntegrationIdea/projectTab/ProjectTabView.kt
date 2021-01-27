package net.ntworld.sentryIntegrationIdea.projectTab

import com.intellij.openapi.Disposable
import net.ntworld.sentryIntegration.entity.IssueQuery
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.View
import java.util.*

interface ProjectTabView: View<ProjectTabView.ActionListener>, Component, Disposable {

    fun addQueryTab(tabName: String, query: IssueQuery, canClose: Boolean, loadIssues: Boolean)

    fun handleMergeIssuesClicked(tabName: String)

    fun displayToolbarActionsForTab(tabName: String)

    fun setTeamsAndUsers(teams: List<SentryTeam>, users: List<SentryUser>)

    interface ActionListener : EventListener {
        fun onMergeIssuesClicked()

        fun onQueryTabSelected(tabName: String, query: IssueQuery)
    }
}