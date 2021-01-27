package net.ntworld.sentryIntegrationIdea.task

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueSortedByEnum
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class FetchIssuesTask(
    projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject,
    private val query: String,
    private val sortedBy: SentryIssueSortedByEnum,
    private val listener: Listener
) : Task.Backgroundable(projectServiceProvider.project, "Fetching sentry issues...", true) {
    fun start() {
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(this, Indicator(this))
    }

    override fun run(indicator: ProgressIndicator) {
        indicator.checkCanceled()

        listener.onDataReceived(SentryApiManager.make(linkedProject).getIssues(query, sortedBy))

        indicator.stop()
    }

    private class Indicator(private val task: FetchIssuesTask) : BackgroundableProcessIndicator(task)

    interface Listener {
        fun onDataReceived(issues: List<SentryIssue>)
    }
}