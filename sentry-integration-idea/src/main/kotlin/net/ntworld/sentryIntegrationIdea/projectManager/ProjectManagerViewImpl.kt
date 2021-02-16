package net.ntworld.sentryIntegrationIdea.projectManager

import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.OnePixelSplitter
import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.AbstractView
import net.ntworld.sentryIntegrationIdea.component.EnvironmentFormComponent
import net.ntworld.sentryIntegrationIdea.panel.ConnectionDetailPanel
import net.ntworld.sentryIntegrationIdea.panel.ProjectDetailPanel
import net.ntworld.sentryIntegrationIdea.panel.UpdateProjectNameForm
import net.ntworld.sentryIntegrationIdea.projectManager.tree.ProjectManagerTreeWrapperComponent
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.util.LinkedProjectUtil
import javax.swing.JComponent

class ProjectManagerViewImpl(
    private val projectServiceProvider: ProjectServiceProvider
) : AbstractView<ProjectManagerView.ActionListener>(), ProjectManagerView {
    override val dispatcher = EventDispatcher.create(ProjectManagerView.ActionListener::class.java)
    private val myComponent = OnePixelSplitter(ProjectManagerView::class.java.canonicalName, 0.35f)
    private val myProjectManagerTreeWrapperComponent = ProjectManagerTreeWrapperComponent(
        projectServiceProvider,
        dispatcher
    )
    private val myProjectDetailPanel = ProjectDetailPanel(projectServiceProvider)
    private val myConnectionDetailPanel = ConnectionDetailPanel()
    private val myUpdateProjectNameForm = UpdateProjectNameForm()

    override val component: JComponent = myComponent

    init {
        myComponent.firstComponent = myProjectManagerTreeWrapperComponent.component
        myComponent.secondComponent = myProjectDetailPanel.component
    }

    override fun displayLinkedProjects(projects: List<LinkedProject>) {
        myProjectManagerTreeWrapperComponent.setLinkedProjects(projects)
    }

    override fun showEditProjectNameDialog(projects: List<LinkedProject>) {
        myUpdateProjectNameForm.myProjectName!!.text = projects.first().name
        val builder = DialogBuilder()
        builder.setCenterPanel(myUpdateProjectNameForm.component)

        builder.removeAllActions()
        builder.addOkAction()
        builder.resizable(false)

        val exitCode = builder.show()
        if (exitCode == DialogWrapper.OK_EXIT_CODE) {
            val name = myUpdateProjectNameForm.myProjectName!!.text.trim()
            if (name.isNotEmpty() && name != projects.first().name) {
                dispatcher.multicaster.onProjectsRenameRequested(projects, name)
            }
        }
    }

    override fun showEditEnvironmentDialog(sentryProjects: List<SentryProject>, project: LinkedProject, allowRenameProject: Boolean) {
        val form = EnvironmentFormComponent(allowRenameProject)
        form.setProjects(sentryProjects)
        form.setProjectName(project.name)
        form.setEnvironmentName(project.environmentName)
        form.setDeployedBranch(project.deployedBranch)
        form.setDeployedRootPath(project.sentryRootPath)
        form.setUseCompiledLanguage(project.useCompiledLanguage)
        form.setSelectedProject(project.sentryProjectId)

        val builder = DialogBuilder()
        builder.setCenterPanel(form.component)

        builder.removeAllActions()
        builder.addOkAction()
        builder.resizable(false)
        val exitCode = builder.show()
        if (exitCode == DialogWrapper.OK_EXIT_CODE) {
            val selectedProject = form.getSelectedProject()
            if (null === selectedProject) {
                return
            }
            val data = LinkedProjectUtil.UpdateFormData(
                id = project.id,
                name = if (allowRenameProject) form.getProjectName() else project.name,
                environmentName = form.getEnvironmentName(),
                sentryProject = selectedProject,
                environmentRootPath = form.getDeployedRootPath(),
                useCompiledLanguage = form.getUseCompiledLanguage(),
                deployedBranch = form.getDeployedBranch()
            )
            dispatcher.multicaster.onUpdateEnvironmentRequest(project, data)
        }
    }

    override fun showEmptyState() {
        myProjectManagerTreeWrapperComponent.selectedConnection = null
        myProjectManagerTreeWrapperComponent.selectedProjectCollection = null
        myProjectManagerTreeWrapperComponent.selectedProject = null
        myProjectDetailPanel.hide()
        myConnectionDetailPanel.hide()
    }

    override fun showConnection(connection: Connection, projects: List<LinkedProject>) {
        myProjectManagerTreeWrapperComponent.selectedConnection = connection
        myProjectManagerTreeWrapperComponent.selectedProjectCollection = projects
        myProjectManagerTreeWrapperComponent.selectedProject = null
        myProjectDetailPanel.hide()
        myConnectionDetailPanel.show(connection)
        myComponent.secondComponent = myConnectionDetailPanel.component
    }

    override fun showProject(projects: List<LinkedProject>) {
        myProjectManagerTreeWrapperComponent.selectedConnection = null
        myProjectManagerTreeWrapperComponent.selectedProjectCollection = projects
        myProjectManagerTreeWrapperComponent.selectedProject = null
        myProjectDetailPanel.displayProjects(projects)
        myComponent.secondComponent = myProjectDetailPanel.component
    }

    override fun showEnvironment(project: LinkedProject) {
        myProjectManagerTreeWrapperComponent.selectedConnection = null
        myProjectManagerTreeWrapperComponent.selectedProjectCollection = null
        myProjectManagerTreeWrapperComponent.selectedProject = project
        myProjectDetailPanel.displayProject(project)
        myComponent.secondComponent = myProjectDetailPanel.component
    }
}