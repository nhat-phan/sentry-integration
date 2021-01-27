package net.ntworld.sentryIntegrationIdea.projectTab.queryTab

import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.Presenter

interface QueryTabPresenter: Presenter<QueryTabPresenter.EventListener>  {
    val model: QueryTabModel

    val view: QueryTabView

    fun isLoaded(): Boolean

    fun refreshIssueTree()

    fun getSelectedIssuesCount(): Int

    fun multipleIssuesActionClicked(action: QueryTabView.MultipleIssuesAction)

    fun setTeamsAndUsers(teams: List<SentryTeam>, users: List<SentryUser>)

    interface EventListener: java.util.EventListener {
        fun onIssuesTreeLoading(tabName: String)

        fun onIssuesTreeRendered(tabName: String, count: Int)

        fun whenSingleIssueSelected(tabName: String, issue: SentryIssue)

        fun whenMultipleIssuesSelected(tabName: String, issues: List<SentryIssue>)
    }
}