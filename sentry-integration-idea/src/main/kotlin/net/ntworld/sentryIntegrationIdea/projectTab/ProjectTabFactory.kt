package net.ntworld.sentryIntegrationIdea.projectTab

import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

object ProjectTabFactory {

    fun makeProjectTabPresenter(
        projectServiceProvider: ProjectServiceProvider,
        linkedProject: LinkedProject
    ): ProjectTabPresenter {
        val model = ProjectTabModelImpl(linkedProject)
        val view = ProjectTabViewImpl(projectServiceProvider, linkedProject)

        return ProjectTabPresenterImpl(projectServiceProvider, view, model)
    }
}