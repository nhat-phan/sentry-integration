package net.ntworld.sentryIntegrationIdea.task

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import net.ntworld.sentryIntegration.StorageManager
import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryIssueHash
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class FetchIssueDetailTask(
    private val projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject,
    private val issue: SentryIssue,
    private val enableCache: Boolean,
    private val listener: Listener
) : Task.Backgroundable(projectServiceProvider.project, "Fetching sentry issue detail...", true) {
    fun start() {
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(this, Indicator(this))
    }

    override fun run(indicator: ProgressIndicator) {
        indicator.checkCanceled()

        val api = SentryApiManager.make(linkedProject, cache = enableCache)
        val detail = api.getIssueDetail(issueId = issue.id)
        listener.onIssueDetailReceived(issue, detail)

        val hashes = api.getIssueHashes(issueId = issue.id)
        val repositoryManager = projectServiceProvider.makeRepositoryManager(linkedProject)
        val storage = StorageManager.make(linkedProject)

        for (i in 0..hashes.lastIndex) {
            val exceptions = hashes[i].latestEventDetail.exceptions
            ApplicationManager.getApplication().runReadAction {
                for (exception in exceptions) {
                    // Calling isSourceFile will cached the result
                    // We need to call here because it's expensive and this one run in background thread
                    for (stacktrace in exception.stacktrace) {
                        repositoryManager.isSourceFile(linkedProject, stacktrace)
                    }
                }
            }

            // Only store the first hash for now
            if (i == 0) {
                storage.store(issue.toReportedIssue(), exceptions.map { it.toReportedException() })
            }
        }
        listener.onIssueHashesReceived(issue, hashes)
        listener.stop(issue)
        indicator.stop()
    }

    private class Indicator(private val task: FetchIssueDetailTask) : BackgroundableProcessIndicator(task)

    interface Listener {
        fun onFetchingIssueDetailHasError(issue: SentryIssue)

        fun onFetchingIssueHashesHasError(issue: SentryIssue)

        fun onIssueDetailReceived(issue: SentryIssue, detail: SentryIssueDetail)

        fun onIssueHashesReceived(issue: SentryIssue, hashes: List<SentryIssueHash>)

        fun stop(issue: SentryIssue)
    }
}