package net.ntworld.sentryIntegrationIdea.projectManager

import com.intellij.openapi.ui.Messages
import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.AbstractSimplePresenter
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.task.ValidateLinkedProjectsTask
import net.ntworld.sentryIntegrationIdea.toolWindow.ProjectTabManager
import net.ntworld.sentryIntegrationIdea.toolWindow.MainToolWindowManager
import net.ntworld.sentryIntegrationIdea.util.LinkedProjectUtil
import javax.swing.JComponent

class ProjectManagerPresenterImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val projectTabManager: ProjectTabManager,
    override val view: ProjectManagerView,
    override val model: ProjectManagerModel
) : AbstractSimplePresenter(), ProjectManagerPresenter, ProjectManagerView.ActionListener,
    ProjectManagerModel.DataListener {
    override val component: JComponent = view.component

    private val myValidateLinkedProjectsTaskListener = object : ValidateLinkedProjectsTask.Listener {
        override fun onLinkedProjectValidated(validated: List<LinkedProject>) {
            projectServiceProvider.updateLinkedProjectsState(validated)
        }
    }

    init {
        view.addActionListener(this)
        model.addDataListener(this)
        view.showEmptyState()
    }

    override fun onProjectsRenameRequested(projects: List<LinkedProject>, newName: String) {
        projectServiceProvider.updateLinkedProjectsState(projects.map {
            it.copy(name = newName)
        })
    }

    override fun onConnectionDeleteRequest(connection: Connection, projects: List<LinkedProject>) {
        val result = Messages.showYesNoDialog(
            "Do you want to delete the selected connection and all projects?", "Are you sure", Messages.getQuestionIcon()
        )
        if (result == Messages.YES) {
            projectServiceProvider.deleteLinkedProjects(projects)
            projectServiceProvider.applicationServiceProvider.deleteConnection(
                projectServiceProvider.project,
                connection
            )
        }
    }

    override fun onProjectDeleteRequest(projects: List<LinkedProject>) {
        val result = Messages.showYesNoDialog(
            "Do you want to delete the selected project and all environments?", "Are you sure", Messages.getQuestionIcon()
        )
        if (result == Messages.YES) {
            projectServiceProvider.deleteLinkedProjects(projects)
        }
    }

    override fun onEnvironmentDeleteRequest(project: LinkedProject) {
        val result = Messages.showYesNoDialog(
            "Do you want to delete the selected environment?", "Are you sure" , Messages.getQuestionIcon()
        )
        if (result == Messages.YES) {
            projectServiceProvider.deleteLinkedProjects(listOf(project))
        }
    }

    override fun onProjectRenameClicked(projects: List<LinkedProject>) {
        view.showEditProjectNameDialog(projects)
    }

    override fun onEnvironmentEditRequest(project: LinkedProject, allowRenameProject: Boolean) {
        if (project.state != LinkedProject.State.READY) {
            return
        }

        val sentryProjects = SentryApiManager.make(project.connection).getAllProjects()
        view.showEditEnvironmentDialog(sentryProjects, project, allowRenameProject)
    }

    override fun onUpdateEnvironmentRequest(project: LinkedProject, data: LinkedProjectUtil.UpdateFormData) {
        val (isValid, message) = LinkedProjectUtil.validateBeforeUpdate(project, projectServiceProvider.linkedProjects, data)
        if (!isValid) {
            return Messages.showErrorDialog(
                message,
                "Invalid Data"
            )
        }

        projectServiceProvider.updateLinkedProject(project.copy(
            name = data.name,
            environmentName = data.environmentName,
            sentryProjectId = data.sentryProject.id,
            sentryProjectSlug = data.sentryProject.slug,
            sentryOrganizationId = data.sentryProject.organization.id,
            sentryOrganizationSlug = data.sentryProject.organization.slug,
            deployedBranch = data.deployedBranch,
            sentryRootPath = data.environmentRootPath,
            state = LinkedProject.State.INITIALIZE
        ))
        view.showEmptyState()
    }

    override fun onAddProjectClicked() {
        projectServiceProvider.project.messageBus.syncPublisher(MainToolWindowManager.TOPIC).requestOpenSetupWizard()
    }

    override fun onOpenProjectsClicked(projects: List<LinkedProject>) {
        projectTabManager.openLinkedProjects(projects)
    }

    override fun onProjectTreeUnselected() {
        view.showEmptyState()
    }

    override fun onProjectSelected(projects: List<LinkedProject>, isSelectConnection: Boolean) {
        if (projects.isEmpty()) {
            return
        }
        if (isSelectConnection) {
            val first = projects.first()
            val connection = projectServiceProvider.connections.firstOrNull { it.id == first.connectionId }
            if (null === connection) {
                view.showConnection(Connection(id = "", token = "", url = "NOT FOUND"), projects)
            } else {
                view.showConnection(connection, projects)
            }
        } else {
            view.showProject(projects)
        }
    }

    override fun onEnvironmentSelected(project: LinkedProject) {
        view.showEnvironment(project)
    }

    override fun whenLinkedProjectsDataChanged() {
        val initializeProjects = model.linkedProjects.filter {
            it.state == LinkedProject.State.INITIALIZE
        }
        ValidateLinkedProjectsTask(
            projectServiceProvider,
            initializeProjects,
            myValidateLinkedProjectsTaskListener
        ).start("ProjectManagerPresenter")

        view.displayLinkedProjects(model.linkedProjects)
    }
}