package net.ntworld.sentryIntegrationIdea.projectManager

import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.LocalRepository
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.View
import net.ntworld.sentryIntegrationIdea.component.ProjectsTreeComponent
import net.ntworld.sentryIntegrationIdea.util.LinkedProjectUtil
import java.util.*

interface ProjectManagerView : View<ProjectManagerView.ActionListener>, Component {

    fun displayLinkedProjects(projects: List<LinkedProject>)

    fun showEditProjectNameDialog(projects: List<LinkedProject>)

    fun showEditEnvironmentDialog(sentryProjects: List<SentryProject>, project: LinkedProject, allowRenameProject: Boolean)

    fun showEmptyState()

    fun showProject(projects: List<LinkedProject>)

    fun showConnection(connection: Connection, projects: List<LinkedProject>)

    fun showEnvironment(project: LinkedProject)

    interface ActionListener : EventListener, ProjectsTreeComponent.Listener {
        fun onProjectsRenameRequested(projects: List<LinkedProject>, newName: String)

        fun onConnectionDeleteRequest(connection: Connection, projects: List<LinkedProject>)

        fun onProjectDeleteRequest(projects: List<LinkedProject>)

        fun onEnvironmentDeleteRequest(project: LinkedProject)

        fun onProjectRenameClicked(projects: List<LinkedProject>)

        fun onEnvironmentEditRequest(project: LinkedProject, allowRenameProject: Boolean)

        fun onUpdateEnvironmentRequest(project: LinkedProject, data: LinkedProjectUtil.UpdateFormData)

        fun onAddProjectClicked()

        fun onOpenProjectsClicked(projects: List<LinkedProject>)
    }
}