package net.ntworld.sentryIntegrationIdea.task

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueMutateParams
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class MutateIssuesTask(
    projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject,
    private val issues: List<SentryIssue>,
    private val params: SentryIssueMutateParams,
    private val listener: Listener
) : Task.Backgroundable(projectServiceProvider.project, "Update sentry issues...", true)  {
    fun start() {
        if (issues.isEmpty()) {
            return
        }
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(this, Indicator(this))
    }

    override fun run(indicator: ProgressIndicator) {
        indicator.checkCanceled()

        SentryApiManager.make(linkedProject).mutateIssues(issues, params)

        if (issues.count() == 1) {
            listener.onSingleIssueMutated(issues.first())
        } else {
            listener.onMultipleIssuesMutated(issues)
        }

        indicator.stop()
    }

    private class Indicator(private val task: MutateIssuesTask) : BackgroundableProcessIndicator(task)

    interface Listener {
        fun onSingleIssueMutated(issue: SentryIssue)

        fun onMultipleIssuesMutated(issues: List<SentryIssue>)
    }
}