package net.ntworld.sentryIntegrationIdea.projectTab.queryTab

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.entity.SentryEventDetail
import net.ntworld.sentryIntegration.entity.SentryEventException
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueAssignee
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryIssueHash
import net.ntworld.sentryIntegration.entity.SentryIssueMutateParams
import net.ntworld.sentryIntegration.entity.SentryIssueSortedByEnum
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.AbstractPresenter
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.task.FetchIssueDetailTask
import net.ntworld.sentryIntegrationIdea.task.FetchIssuesTask
import net.ntworld.sentryIntegrationIdea.task.MutateIssuesTask
import java.awt.datatransfer.StringSelection
import java.util.*

class QueryTabPresenterImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    override val model: QueryTabModel,
    override val view: QueryTabView
) : AbstractPresenter<QueryTabPresenter.EventListener>(),
    QueryTabPresenter, QueryTabModel.DataListener, QueryTabView.ActionListener {
    override val dispatcher = EventDispatcher.create(QueryTabPresenter.EventListener::class.java)
    private var myIsInitialized = false
    private val myFetchingIssueDetailSet = Collections.synchronizedSet(mutableSetOf<String>())
    private val myFetchIssuesTaskListener = object : FetchIssuesTask.Listener {
        override fun onDataReceived(issues: List<SentryIssue>) {
            model.setIssues(issues)
            view.displayIssues(issues, model.eventDetailsMap, model.displayEventCount)
            dispatcher.multicaster.onIssuesTreeRendered(model.name, issues.count())
        }
    }
    private val myFetchIssueDetailTaskListener = object : FetchIssueDetailTask.Listener {
        override fun onFetchingIssueDetailHasError(issue: SentryIssue) {
        }

        override fun onFetchingIssueHashesHasError(issue: SentryIssue) {
        }

        override fun onIssueDetailReceived(issue: SentryIssue, detail: SentryIssueDetail) {
            model.setIssueDetail(issue.id, detail)
            view.displayIssueDetail(issue, detail)
        }

        override fun onIssueHashesReceived(issue: SentryIssue, hashes: List<SentryIssueHash>) {
            if (hashes.count() > 0) {
                model.setEventDetail(issue.id, hashes.first().latestEventDetail)
                view.displayEventDetail(issue, hashes.first().latestEventDetail)
            }
        }

        override fun stop(issue: SentryIssue) {
            myFetchingIssueDetailSet.add(issue.id)
            if (!issue.hasSeen && projectServiceProvider.pluginConfiguration.markIssueAsSeenAutomatically) {
                onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.MARK_AS_SEEN, null)
            }
        }
    }
    private val myMutateIssuesTaskListener = object : MutateIssuesTask.Listener {
        override fun onSingleIssueMutated(issue: SentryIssue) {
            model.updateSingleIssueState(issue)
        }

        override fun onMultipleIssuesMutated(issues: List<SentryIssue>) {
            refreshIssueTree()
        }
    }

    init {
        view.addActionListener(this)
        model.addDataListener(this)
    }

    override fun isLoaded(): Boolean = myIsInitialized

    override fun refreshIssueTree() {
        dispatcher.multicaster.onIssuesTreeLoading(model.name)
        val task = FetchIssuesTask(
            projectServiceProvider,
            model.linkedProject,
            model.query,
            model.sortedBy,
            myFetchIssuesTaskListener
        )

        task.start()
        model.clearIssueDetailData()
        myFetchingIssueDetailSet.clear()
        myIsInitialized = true
        // Next is myFetchIssuesTaskListener@onDataReceived
    }

    override fun getSelectedIssuesCount(): Int {
        return model.selectedIssueIds.count()
    }

    override fun whenSingleIssueStateUpdated(issue: SentryIssue) {
        view.displayIssues(model.issues, model.eventDetailsMap, model.displayEventCount)
        view.updateIssueIfDetailIsOpened(issue)
    }

    override fun whenQueryChanged(query: String) {
        ApplicationManager.getApplication().invokeLater {
            refreshIssueTree()
        }
    }

    override fun whenSortedByChanged(sortedBy: SentryIssueSortedByEnum) {
        ApplicationManager.getApplication().invokeLater {
            refreshIssueTree()
        }
    }

    override fun whenDisplayEventCountChanged(display: Boolean) {
        ApplicationManager.getApplication().invokeLater {
            view.displayIssues(model.issues, model.eventDetailsMap, model.displayEventCount)
        }
    }

    override fun whenEventDetailsMapChanged(eventDetailsMap: Map<String, SentryEventDetail>) {
        view.displayIssues(model.issues, eventDetailsMap, model.displayEventCount)
    }

    override fun whenSelectedIssueIdsChanged(issueIds: List<String>) {}

    override fun onQueryChanged(query: String, refreshClicked: Boolean) {
        if (model.query == query) {
            if (refreshClicked) {
                refreshIssueTree()
            }
        } else {
            model.query = query // will trigger the refreshIssueTree by whenQueryChanged() data event
        }
    }

    override fun onSortedByChanged(sortedBy: SentryIssueSortedByEnum) {
        model.sortedBy = sortedBy // will trigger the refreshIssueTree by whenSortedByChanged() data event
    }

    override fun onDisplayEventCountChanged(display: Boolean) {
        model.displayEventCount = display // will trigger the refreshIssueTree by whenEventDetailsMapChanged() data event
    }

    override fun onStacktraceSelected(
        issue: SentryIssue,
        exception: SentryEventException,
        stacktrace: SentryEventExceptionStacktrace,
        index: Int
    ) {
        projectServiceProvider.editorManager.open(
            FrameDataBuilder.build(
                model.linkedProject,
                issue,
                exception,
                stacktrace,
                index,
                Storage.FrameSource.MAIN_UI
            )
        )
    }

    override fun onIssuesSelected(issues: List<SentryIssue>) {
        model.setSelectedIssueIds(issues.map { it.id })
        if (issues.count() == 0) {
            view.hideIssueDetail()
            return
        }

        if (issues.count() > 1) {
            view.hideIssueDetail()
            dispatcher.multicaster.whenMultipleIssuesSelected(model.name, issues)
            return
        }

        val first = issues.first()
        dispatcher.multicaster.whenSingleIssueSelected(model.name, first)
        view.displaySingleIssueToolbar(first)
        handleIssueTreeDetailState(first)
    }

    override fun onIssueNodeExpanded(issue: SentryIssue) {
        handleIssueTreeDetailState(issue)
    }

    override fun onIssueNodeCollapsed(issue: SentryIssue) {
        handleIssueTreeDetailState(issue)
    }

    override fun multipleIssuesActionClicked(action: QueryTabView.MultipleIssuesAction) {
        val issues = model.collectSelectedIssues()
        if (issues.count() > 1) {
            when (action) {
                QueryTabView.MultipleIssuesAction.MERGED -> {
                    makeMutateIssuesTask(issues, SentryIssueMutateParams(merge = true)).start()
                }
                QueryTabView.MultipleIssuesAction.RESOLVE -> {
                }
            }
        }
    }

    override fun setTeamsAndUsers(teams: List<SentryTeam>, users: List<SentryUser>) {
        view.setTeamsAndUsers(teams, users)
    }

    override fun onSingleIssueActionClicked(issue: SentryIssue, action: QueryTabView.SingleIssueAction, params: Any?) {
        when (action) {
            QueryTabView.SingleIssueAction.CHANGE_ASSIGNEE -> {
                if (params is Pair<*, *>) {
                    val nextState = issue.copy(assignedTo = params.second as SentryIssueAssignee?)
                    makeMutateIssuesTask(listOf(nextState), SentryIssueMutateParams(assignedTo = params.first as String)).start()
                }
            }
            QueryTabView.SingleIssueAction.OPEN_IN_BROWSER -> {
                BrowserUtil.open(issue.permalink)
            }
            QueryTabView.SingleIssueAction.COPY_LINK -> {
                CopyPasteManager.getInstance().setContents(StringSelection(issue.permalink))
            }
            QueryTabView.SingleIssueAction.RESOLVE -> {
                val nextState = issue.copy(status = "resolved")
                makeMutateIssuesTask(listOf(nextState), SentryIssueMutateParams(status = "resolved")).start()
            }
            QueryTabView.SingleIssueAction.UNRESOLVE -> {
                val nextState = issue.copy(status = "unresolved")
                makeMutateIssuesTask(listOf(nextState), SentryIssueMutateParams(status = "unresolved")).start()
            }
            QueryTabView.SingleIssueAction.MARK_AS_SEEN -> {
                val nextState = issue.copy(hasSeen = true)
                makeMutateIssuesTask(listOf(nextState), SentryIssueMutateParams(hasSeen = true)).start()
            }
            QueryTabView.SingleIssueAction.MARK_AS_UNSEEN -> {
                val nextState = issue.copy(hasSeen = false)
                makeMutateIssuesTask(listOf(nextState), SentryIssueMutateParams(hasSeen = false)).start()
            }
            QueryTabView.SingleIssueAction.BOOKMARK -> {
                val nextState = issue.copy(isBookmarked = true)
                makeMutateIssuesTask(listOf(nextState), SentryIssueMutateParams(isBookmarked = true)).start()
            }
            QueryTabView.SingleIssueAction.REMOVE_BOOKMARK -> {
                val nextState = issue.copy(isBookmarked = false)
                makeMutateIssuesTask(listOf(nextState), SentryIssueMutateParams(isBookmarked = false)).start()
            }
            QueryTabView.SingleIssueAction.SUBSCRIBE -> {
                val nextState = issue.copy(isSubscribed = true)
                makeMutateIssuesTask(listOf(nextState), SentryIssueMutateParams(isSubscribed = true)).start()
            }
            QueryTabView.SingleIssueAction.UNSUBSCRIBE -> {
                val nextState = issue.copy(isSubscribed = false)
                makeMutateIssuesTask(listOf(nextState), SentryIssueMutateParams(isSubscribed = false)).start()
            }
            QueryTabView.SingleIssueAction.IGNORE -> {
            }
        }
    }

    private fun makeMutateIssuesTask(issues: List<SentryIssue>, params: SentryIssueMutateParams) = MutateIssuesTask(
        projectServiceProvider,
        model.linkedProject,
        issues,
        params,
        myMutateIssuesTaskListener
    )

    private fun handleIssueTreeDetailState(issue: SentryIssue, enableCache: Boolean = true) {
        if (model.isIssueDetailLoaded(issue)) {
            if (view.isIssueNodeExpanded(issue)) {
                val issueDetail = model.issueDetailsMap[issue.id]
                val eventDetail = model.eventDetailsMap[issue.id]
                if (null !== issueDetail || null !== eventDetail) {
                    view.displayIssueDetailLoadingState(issue)
                    if (null !== issueDetail) {
                        view.displayIssueDetail(issue, issueDetail)
                    }
                    if (null !== eventDetail) {
                        view.displayEventDetail(issue, eventDetail)
                    }
                    return
                }
            } else {
                view.hideIssueDetail()
            }
        }

        if (myFetchingIssueDetailSet.contains(issue.id)) {
            return
        }

        ApplicationManager.getApplication().invokeLater {
            if (view.isIssueNodeExpanded(issue)) {
                view.displayIssueDetailLoadingState(issue)
                myFetchingIssueDetailSet.add(issue.id)
                val task = FetchIssueDetailTask(
                    projectServiceProvider,
                    model.linkedProject,
                    issue,
                    enableCache,
                    myFetchIssueDetailTaskListener
                )
                task.start()
                model.markIssueDetailLoadStatus(issue, true)
            }
        }
    }
}