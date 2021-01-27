package net.ntworld.sentryIntegrationIdea.projectTab

import com.intellij.openapi.util.Disposer
import net.ntworld.sentryIntegration.debug
import net.ntworld.sentryIntegration.entity.IssueQuery
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.AbstractSimplePresenter
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.task.FetchTeamsAndUsersTask
import javax.swing.JComponent

class ProjectTabPresenterImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val view: ProjectTabView,
    private val model: ProjectTabModel
) : AbstractSimplePresenter(), ProjectTabPresenter, ProjectTabView.ActionListener,
    ProjectTabModel.DataListener {
    override val component: JComponent
        get() = view.component
    override val linkedProject: LinkedProject
        get() = model.linkedProject

    private val myFetchTeamsAndUsersTaskListener = object : FetchTeamsAndUsersTask.Listener {
        override fun onDataReceived(teams: List<SentryTeam>, users: List<SentryUser>) {
            model.teams = teams
            model.users = users
            view.setTeamsAndUsers(teams, users)
        }
    }

    init {
        view.addActionListener(this)
        model.addDataListener(this)
        view.addQueryTab("Unresolved", IssueQuery(resolved = false), canClose = false, loadIssues = true)
        view.addQueryTab("Unresolved and Unassigned", IssueQuery(resolved = false, isAssigned = false), canClose = false, loadIssues = false)
        view.addQueryTab("Assigned to Me", IssueQuery(resolved = false, assignedToMe = true), canClose = false, loadIssues = false)
        view.addQueryTab("Last 4 Hours", IssueQuery(resolved = false, lastSeen = "-4h"), canClose = false, loadIssues = false)
        view.addQueryTab("Bookmarked", IssueQuery(resolved = false, bookmarked = true), canClose = false, loadIssues = false)
        fetchingTeamsAndUsers(true)

        debug("DISPOSER: register (ProjectTabPresenterImpl > view)")
        Disposer.register(this, view)
    }

    override fun dispose() {
        debug("ProjectTabPresenterImpl::dispose()")
    }

    override fun onMergeIssuesClicked() {
        // This is handled by QueryTabPresenter, not ProjectTabPresenter
        view.handleMergeIssuesClicked(model.selectedTabName)
    }

    override fun onQueryTabSelected(tabName: String, query: IssueQuery) {
        model.selectedTabName = tabName
        view.displayToolbarActionsForTab(tabName)
    }

    private fun fetchingTeamsAndUsers(enabledCache: Boolean) {
        val task = FetchTeamsAndUsersTask(
            projectServiceProvider,
            model.linkedProject,
            enabledCache,
            myFetchTeamsAndUsersTaskListener
        )
        task.start()
    }
}