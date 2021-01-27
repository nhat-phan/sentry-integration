package net.ntworld.sentryIntegrationIdea.projectTab.queryTab

import net.ntworld.sentryIntegration.entity.IssueQuery
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

object QueryTabFactory {

    fun makeQueryTab(
        projectServiceProvider: ProjectServiceProvider,
        linkedProject: LinkedProject,
        name: String,
        query: IssueQuery
    ): QueryTabPresenter {
        val model = QueryTabModelImpl(linkedProject, name, query)
        val view = QueryTabViewImpl(projectServiceProvider, linkedProject, query.toQueryString(), name)

        return QueryTabPresenterImpl(projectServiceProvider, model, view)
    }

}