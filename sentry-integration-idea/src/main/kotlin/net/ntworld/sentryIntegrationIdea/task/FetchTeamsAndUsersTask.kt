package net.ntworld.sentryIntegrationIdea.task

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class FetchTeamsAndUsersTask(
    private val projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject,
    private val enableCache: Boolean,
    private val listener: Listener
): Task.Backgroundable(projectServiceProvider.project, "Fetching teams and users...", true) {
    fun start() {
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(this, Indicator(this))
    }

    override fun run(indicator: ProgressIndicator) {
        indicator.checkCanceled()

        val api = SentryApiManager.make(linkedProject, cache = enableCache)
        val teams = api.getTeams()
        val users = api.getUsers()
        listener.onDataReceived(teams, users)

        indicator.stop()
    }

    private class Indicator(private val task: FetchTeamsAndUsersTask) : BackgroundableProcessIndicator(task)

    interface Listener {
        fun onDataReceived(teams: List<SentryTeam>, users: List<SentryUser>)

    }
}