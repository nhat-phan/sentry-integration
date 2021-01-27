package net.ntworld.sentryIntegrationIdea

import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.component.IssueStatComponent
import net.ntworld.sentryIntegrationIdea.component.IssueStatComponentImpl
import net.ntworld.sentryIntegrationIdea.component.ProjectsTreeComponent
import net.ntworld.sentryIntegrationIdea.component.ProjectsTreeComponentImpl
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component.IssuesTreeComponent
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component.IssuesTreeComponentImpl
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

object ComponentFactory {
    fun makeProjectsTreeComponent(projectServiceProvider: ProjectServiceProvider): ProjectsTreeComponent {
        return ProjectsTreeComponentImpl(projectServiceProvider)
    }

    fun makeIssuesTreeComponent(projectServiceProvider: ProjectServiceProvider, linkedProject: LinkedProject): IssuesTreeComponent {
        return IssuesTreeComponentImpl(projectServiceProvider, linkedProject)
    }

    fun makeIssueStatComponent(style: IssueStatComponent.Style): IssueStatComponent {
        return IssueStatComponentImpl(style)
    }
}