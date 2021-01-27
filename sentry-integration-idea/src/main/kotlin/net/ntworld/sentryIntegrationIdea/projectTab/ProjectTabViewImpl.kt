package net.ntworld.sentryIntegrationIdea.projectTab

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.TabsListener
import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.IssueQuery
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.AbstractView
import net.ntworld.sentryIntegrationIdea.component.TabsComponent
import net.ntworld.sentryIntegrationIdea.component.TabsComponentImpl
import net.ntworld.sentryIntegrationIdea.projectTab.component.ProjectTabToolbarComponent
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.QueryTabFactory
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.QueryTabPresenter
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.QueryTabView
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import javax.swing.JComponent
import javax.swing.JPanel

class ProjectTabViewImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject
) : AbstractView<ProjectTabView.ActionListener>(), ProjectTabView {
    override val dispatcher = EventDispatcher.create(ProjectTabView.ActionListener::class.java)
    private val MAX_COUNT_PER_PAGE = 100
    private val myComponent = SimpleToolWindowPanel(false, false)
    private val myProjectTabToolbarComponent = ProjectTabToolbarComponent(projectServiceProvider, linkedProject, dispatcher)
    private val myTabMap = mutableMapOf<String, MyTab>()
    private val myQueryTabPresenterMap = mutableMapOf<String, QueryTabPresenter>()
    private var mySelectedTab: String = ""
    private val myQueryTabPresenterEventListener = object : QueryTabPresenter.EventListener {
        override fun onIssuesTreeLoading(tabName: String) {
            val tab = myTabMap[tabName]
            if (null !== tab) {
                ApplicationManager.getApplication().invokeLater {
                    tab.tabInfo.text = "$tabName · Loading..."
                }
            }
        }

        override fun onIssuesTreeRendered(tabName: String, count: Int) {
            val tab = myTabMap[tabName]
            if (null !== tab) {
                ApplicationManager.getApplication().invokeLater {
                    val countText = if (count == MAX_COUNT_PER_PAGE) "100+" else count.toString()
                    tab.tabInfo.text = "$tabName · $countText"
                }
            }
        }

        override fun whenSingleIssueSelected(tabName: String, issue: SentryIssue) {
            myProjectTabToolbarComponent.disableMultipleIssuesActions()
        }

        override fun whenMultipleIssuesSelected(tabName: String, issues: List<SentryIssue>) {
            myProjectTabToolbarComponent.enableMultipleIssuesActions()
        }
    }
    private val myTabsComponent: TabsComponent by lazy {
        val tabs = TabsComponentImpl(projectServiceProvider.project, this)
        tabs.setCommonCenterActionGroupFactory(myCommonCenterActionGroupFactory)
        tabs
    }
    private val myTabsListener = object : TabsListener {
        override fun selectionChanged(oldSelection: TabInfo?, newSelection: TabInfo?) {
            if (null !== newSelection) {
                val tab = findTab(newSelection)
                if (null !== tab) {
                    val queryTabPresenter = myQueryTabPresenterMap[tab.tabName]
                    if (null !== queryTabPresenter && !queryTabPresenter.isLoaded()) {
                        queryTabPresenter.refreshIssueTree()
                    }

                    mySelectedTab = tab.tabName
                    dispatcher.multicaster.onQueryTabSelected(tab.tabName, tab.query)
                }
            }
        }

        private fun findTab(tabInfo: TabInfo): MyTab? {
            for (tab in myTabMap) {
                if (tab.value.tabInfo === tabInfo) {
                    return tab.value
                }
            }
            return null
        }
    }
    private val myCommonCenterActionGroupFactory: () -> ActionGroup = {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(object : AnAction("New Tab", "Add new tab with custom query", AllIcons.General.Add) {
            override fun actionPerformed(e: AnActionEvent) {
            }

            override fun update(e: AnActionEvent) {
                e.presentation.isEnabled = false
                e.presentation.text = "Custom Tab Feature - coming soon"
                e.presentation.description = "Custom Tab Feature - coming soon"
            }
        })
        actionGroup
    }

    override val component: JComponent = myComponent

    init {
        myTabsComponent.addListener(myTabsListener)
        myComponent.setContent(myTabsComponent.component)
        myComponent.toolbar = myProjectTabToolbarComponent.component
    }

    override fun addQueryTab(tabName: String, query: IssueQuery, canClose: Boolean, loadIssues: Boolean) {
        val name = tabName.trim()
        if (name.isEmpty()) {
            return
        }

        if (!myTabMap.containsKey(name)) {
            val presenter = QueryTabFactory.makeQueryTab(
                projectServiceProvider,
                linkedProject,
                tabName,
                query
            )
            presenter.addListener(myQueryTabPresenterEventListener)
            myQueryTabPresenterMap[name] = presenter

            val tabInfo = TabInfo(presenter.view.component)
            tabInfo.text = name
            if (loadIssues) {
                presenter.refreshIssueTree()
            } else {
                tabInfo.text = "[$name]"
                tabInfo.tooltipText = "Click to load issues"
            }

            myTabMap[name] = MyTab(name, tabInfo, query, canClose)
            myTabsComponent.addTab(tabInfo)
        }
    }

    override fun handleMergeIssuesClicked(tabName: String) {
        val presenter = myQueryTabPresenterMap[tabName]
        if (null !== presenter) {
            presenter.multipleIssuesActionClicked(QueryTabView.MultipleIssuesAction.MERGED)
        }
    }

    override fun displayToolbarActionsForTab(tabName: String) {
        val presenter = myQueryTabPresenterMap[tabName]
        if (null !== presenter && presenter.getSelectedIssuesCount() > 1) {
            myProjectTabToolbarComponent.enableMultipleIssuesActions()
        } else {
            myProjectTabToolbarComponent.disableMultipleIssuesActions()
        }
    }

    override fun setTeamsAndUsers(teams: List<SentryTeam>, users: List<SentryUser>) {
        for (item in myQueryTabPresenterMap) {
            item.value.setTeamsAndUsers(teams, users)
        }
    }

    override fun dispose() {
    }

    private class MyTab(
        val tabName: String,
        val tabInfo: TabInfo,
        val query: IssueQuery,
        val canClose: Boolean
    )
}
