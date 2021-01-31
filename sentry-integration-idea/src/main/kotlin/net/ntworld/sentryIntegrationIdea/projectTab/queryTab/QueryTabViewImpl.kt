package net.ntworld.sentryIntegrationIdea.projectTab.queryTab

import com.intellij.ui.OnePixelSplitter
import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryEventDetail
import net.ntworld.sentryIntegration.entity.SentryEventException
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.AbstractView
import net.ntworld.sentryIntegrationIdea.CustomSimpleToolWindowPanel
import net.ntworld.sentryIntegrationIdea.node.issue.IssuesTreeData
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component.IssueDetailComponent
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component.IssuesTreeComponent
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component.IssuesTreeWrapperComponent
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component.SingleIssueToolbarComponent
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

/**
 * Layout of QueryTabView
 *  + QueryTabView (CustomSimpleToolWindowPanel)
 *      + OnePixelSplitter
 *        + TreeWrapper (SimpleToolWindowPanel)
 *          - TreeToolbar (Toolbar)
 *          - IssuesTree (Component)
 *        - Detail (Component)
 *      - SingleIssueToolbar (Toolbar)
 */
class QueryTabViewImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject,
    private val initializeQuery: String,
    private val name: String
) : AbstractView<QueryTabView.ActionListener>(), QueryTabView {
    override val dispatcher = EventDispatcher.create(QueryTabView.ActionListener::class.java)
    override val component = CustomSimpleToolWindowPanel(vertical = false)
    private var myDisplayedIssue: SentryIssue? = null
    private var myDisplayedIssueDetail: SentryIssueDetail? = null
    private var myDisplayedEventDetail: SentryEventDetail? = null

    private val mySplitter = OnePixelSplitter(
        QueryTabViewImpl::class.java.canonicalName + "/${linkedProject.id}@$name",
        0.75f
    )
    private val myIssuesTreeWrapperComponent = IssuesTreeWrapperComponent(
        projectServiceProvider,
        linkedProject,
        initializeQuery,
        name,
        dispatcher
    )
    private val myIssueDetailComponent = IssueDetailComponent(projectServiceProvider.pluginConfiguration, dispatcher)
    private val mySingleIssueToolbarComponent = SingleIssueToolbarComponent(linkedProject, dispatcher)
    private val myIssuesTreeComponentListener = object : IssuesTreeComponent.Listener {
        override fun onIssuesSelected(issues: List<SentryIssue>) {
            dispatcher.multicaster.onIssuesSelected(issues)
        }

        override fun onStacktraceSelected(
            issue: SentryIssue,
            exception: SentryEventException,
            stacktrace: SentryEventExceptionStacktrace,
            index: Int
        ) {
            dispatcher.multicaster.onStacktraceSelected(issue, exception, stacktrace, index)
        }

        override fun onIssueNodeExpanded(issue: SentryIssue) {
            dispatcher.multicaster.onIssueNodeExpanded(issue)
        }

        override fun onIssueNodeCollapsed(issue: SentryIssue) {
            dispatcher.multicaster.onIssueNodeCollapsed(issue)
        }
    }

    init {
        myIssueDetailComponent.component.preferredSize.width = 380
        mySplitter.firstComponent = myIssuesTreeWrapperComponent.component
        mySplitter.secondComponent = myIssueDetailComponent.component

        component.setContent(mySplitter)
        component.toolbar = mySingleIssueToolbarComponent.component
        myIssuesTreeWrapperComponent.tree.addActionListener(myIssuesTreeComponentListener)
        myIssueDetailComponent.component.isVisible = false
    }

    override fun displayIssueDetail(issue: SentryIssue, issueDetail: SentryIssueDetail) {
        myDisplayedIssue = issue
        myDisplayedIssueDetail = issueDetail
        myIssueDetailComponent.setIssueDetail(issue, issueDetail)
        mySingleIssueToolbarComponent.setIssue(issue)
        myIssueDetailComponent.component.isVisible = true
    }

    override fun displayEventDetail(issue: SentryIssue, eventDetail: SentryEventDetail) {
        myDisplayedIssue = issue
        myDisplayedEventDetail = eventDetail
        myIssueDetailComponent.setEventDetail(issue, eventDetail)
        mySingleIssueToolbarComponent.setIssue(issue)
        myIssueDetailComponent.component.isVisible = true
    }

    override fun displayIssueDetailLoadingState(issue: SentryIssue) {
        myDisplayedIssue = issue
        myIssueDetailComponent.displayLoading()

        myIssueDetailComponent.component.isVisible = true
    }

    override fun displaySingleIssueToolbar(issue: SentryIssue) {
        val displayedIssue = myDisplayedIssue
        // If current issue is displayed, so do not hide the detail
        if (null !== displayedIssue && displayedIssue.id == issue.id) {
            return
        }
        mySingleIssueToolbarComponent.setIssue(issue)
        myIssueDetailComponent.component.isVisible = false
    }

    override fun hideIssueDetail() {
        myIssueDetailComponent.component.isVisible = false
    }

    override fun displayIssues(
        issues: List<SentryIssue>,
        eventDetailsMap: Map<String, SentryEventDetail>,
        displayEventCount: Boolean
    ) {
        myIssuesTreeWrapperComponent.tree.setData(IssuesTreeData(issues, eventDetailsMap, displayEventCount))
    }

    override fun isIssueNodeExpanded(issue: SentryIssue): Boolean {
        return myIssuesTreeWrapperComponent.tree.isIssueNodeExpanded(issue)
    }

    override fun updateIssueIfDetailIsOpened(issue: SentryIssue) {
        val displayedIssue = myDisplayedIssue
        if (null !== displayedIssue && displayedIssue.id == issue.id) {
            myDisplayedIssue = issue
            mySingleIssueToolbarComponent.setIssue(issue)

            val displayedIssueDetail = myDisplayedIssueDetail
            if (null !== displayedIssueDetail && displayedIssueDetail.id == issue.id) {
                myIssueDetailComponent.setIssueDetail(issue, displayedIssueDetail)
            }

            val displayedEventDetail = myDisplayedEventDetail
            if (null !== displayedEventDetail && displayedEventDetail.issueId == issue.id) {
                myIssueDetailComponent.setEventDetail(issue, displayedEventDetail)
            }
        }
    }

    override fun setTeamsAndUsers(teams: List<SentryTeam>, users: List<SentryUser>) {
        myIssueDetailComponent.setTeamsAndUsers(teams, users)
    }
}