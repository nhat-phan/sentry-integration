package net.ntworld.sentryIntegrationIdea.projectManager

import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.toolWindow.ProjectTabManager

object ProjectManagerFactory {
    fun makeProjectManagerPresenter(
        projectServiceProvider: ProjectServiceProvider,
        projectTabManager: ProjectTabManager
    ): ProjectManagerPresenter {
        val view = ProjectManagerViewImpl(projectServiceProvider)
        val model = ProjectManagerModelImpl(projectServiceProvider)
        val presenter = ProjectManagerPresenterImpl(
            projectServiceProvider, projectTabManager, view, model
        )
        model.linkedProjects = projectServiceProvider.linkedProjects

        return presenter
    }
}