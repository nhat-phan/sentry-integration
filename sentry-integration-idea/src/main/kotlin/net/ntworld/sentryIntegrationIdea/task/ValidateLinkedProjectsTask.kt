package net.ntworld.sentryIntegrationIdea.task

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.debug
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class ValidateLinkedProjectsTask(
    private val projectServiceProvider: ProjectServiceProvider,
    private val linkedProjects: List<LinkedProject>,
    private val listener: Listener
): Task.Backgroundable(projectServiceProvider.project, "Validate linked sentry projects...", true) {
    private val myLogger = Logger.getInstance(this.javaClass)

    fun start(source: String) {
        myLogger.info("start ValidateLinkedProjectsTask by $source")
        debug("start ValidateLinkedProjectsTask by $source")
        if (linkedProjects.isEmpty()) {
            myLogger.debug("linkedProjects is empty, skipped")
            debug("linkedProjects is empty, skipped")
            return
        }

        ProgressManager.getInstance().runProcessWithProgressAsynchronously(
            this,
            Indicator(this)
        )
    }

    override fun run(indicator: ProgressIndicator) {
        myLogger.info("ValidateLinkedProjectsTask started")
        debug("ValidateLinkedProjectsTask started")
        indicator.checkCanceled()

        val validated = linkedProjects.map {
            validateLinkedProject(it)
        }

        listener.onLinkedProjectValidated(validated)
        myLogger.info("ValidateLinkedProjectsTask stopping")
        debug("ValidateLinkedProjectsTask stopping")
        indicator.stop()
    }

    private fun validateLinkedProject(linkedProject: LinkedProject): LinkedProject {
        val connection = findConnection(linkedProject)
        if (null === connection) {
            return linkedProject.copy(state = LinkedProject.State.INVALID_CONNECTION)
        }

        // Skip validate LocalRepository for now, it will be handled in Editor Controller
//        if (!validateLocalRepository(linkedProject)) {
//            return linkedProject.copy(state = LinkedProject.State.INVALID_LOCAL_ROOT_PATH)
//        }

        try {
            val api = SentryApiManager.make(
                linkedProject.copy(connectionUrl = connection.url, connectionToken = connection.token),
                cache = false
            )
            val project = api.getProject()
            return linkedProject.copy(
                connectionUrl = connection.url,
                connectionToken = connection.token,
                connectionScope = connection.scope,
                sentryProjectId = project.id,
                sentryOrganizationId = project.organization.id,
                state = LinkedProject.State.READY
            )
        } catch (exception: Exception) {
            return linkedProject.copy(state = LinkedProject.State.INVALID_SENTRY_PROJECT)
        }
    }

    private fun findConnection(linkedProject: LinkedProject): Connection? {
        for (connection in projectServiceProvider.connections) {
            if (connection.id == linkedProject.connectionId) {
                return connection
            }
        }
        return null
    }

    private fun validateLocalRepository(linkedProject: LinkedProject): Boolean {
        val localRepositories = projectServiceProvider.getLocalRepositories()
        for (repository in localRepositories) {
            if (repository.path == linkedProject.localRootPath) {
                return true
            }
        }
        return false
    }

    private class Indicator(task: ValidateLinkedProjectsTask) : BackgroundableProcessIndicator(task)

    interface Listener {
        fun onLinkedProjectValidated(validated: List<LinkedProject>)
    }
}