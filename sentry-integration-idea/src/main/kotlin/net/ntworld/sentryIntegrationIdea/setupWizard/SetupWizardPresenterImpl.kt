package net.ntworld.sentryIntegrationIdea.setupWizard

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.AbstractSimplePresenter
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.toolWindow.MainToolWindowManager
import net.ntworld.sentryIntegrationIdea.util.LinkedProjectUtil
import javax.swing.JComponent

class SetupWizardPresenterImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val model: SetupWizardModel,
    private val view: SetupWizardView
) : AbstractSimplePresenter(), SetupWizardPresenter, SetupWizardView.ActionListener, SetupWizardModel.DataListener {
    override val component: JComponent = view.component
    private var mySelectedConnection: Connection? = null
    private var myLocalRootPath: String? = null
    private var myProjectName: String? = null

    init {
        model.addDataListener(this)
        view.addActionListener(this)

        if (projectServiceProvider.connections.isNotEmpty()) {
            view.displaySelectConnectionStep(projectServiceProvider.connections, null)
        } else {
            view.displayCreateConnectionForm(showCancelButton = false)
        }
    }

    override fun onCreateConnectionButtonClicked() {
        view.displayCreateConnectionForm(showCancelButton = projectServiceProvider.connections.isNotEmpty())
    }

    override fun onCreateConnectionCancelClicked() {
        view.displaySelectConnectionStep(projectServiceProvider.connections, null)
    }

    override fun onConnectionTested(connection: Connection) {
        projectServiceProvider.applicationServiceProvider.saveConnection(projectServiceProvider.project, connection)
        mySelectedConnection = connection

        view.displaySelectConnectionStep(projectServiceProvider.connections, connection.id)
        view.displaySelectProjectStep(projectServiceProvider.getLocalRepositories(), projectServiceProvider.linkedProjects)
    }

    override fun onConnectionSelected(connection: Connection) {
        mySelectedConnection = connection

        view.displaySelectProjectStep(projectServiceProvider.getLocalRepositories(), projectServiceProvider.linkedProjects)
    }

    override fun onProjectSelected(localRootPath: String, name: String) {
        val connection = mySelectedConnection
        if (null === connection) {
            return
        }
        myLocalRootPath = localRootPath
        myProjectName = name

        view.displayLoadingStateInFillEnvironmentForm()
        ApplicationManager.getApplication().invokeLater {
            val projects = SentryApiManager.make(connection).getAllProjects()
            view.displayFillEnvironmentInformationStep(projects, filterLinkedProjectByLocalRootPath())
        }
    }

    override fun onEnvironmentFilled(
        environmentName: String,
        sentryProject: SentryProject,
        deployedBranch: String,
        deployedRootPath: String
    ) {
        val connection = mySelectedConnection
        if (null === connection) {
            return
        }
        val localRootPath = myLocalRootPath
        if (null === localRootPath) {
            return
        }
        val projectName = myProjectName
        if (null === projectName) {
            return
        }
        val data = LinkedProjectUtil.CreateFormData(
            connection = connection,
            name = projectName,
            localRootPath = localRootPath,
            sentryProject = sentryProject,
            environmentName = environmentName,
            environmentRootPath = deployedRootPath,
            deployedBranch = deployedBranch
        )
        val (isValid, message) = LinkedProjectUtil.validateBeforeCreating(projectServiceProvider.linkedProjects, data)
        if (!isValid) {
            return Messages.showErrorDialog(
                message,
                "Invalid Data"
            )
        }
        projectServiceProvider.addLinkedProject(
            name = data.name,
            connection = data.connection,
            environmentName = data.environmentName,
            sentryProject = data.sentryProject,
            sentryRootPath = data.environmentRootPath,
            localRootPath = data.localRootPath,
            deployedBranch = data.deployedBranch,
            enableWorker = false
        )
        projectServiceProvider.project.messageBus.syncPublisher(MainToolWindowManager.TOPIC).requestCloseSetupWizard()
    }

    private fun filterLinkedProjectByLocalRootPath(): List<LinkedProject> {
        val localRootPath = myLocalRootPath
        if (null === localRootPath) {
            return projectServiceProvider.linkedProjects
        }
        return projectServiceProvider.linkedProjects.filter {
            it.localRootPath == localRootPath
        }
    }
}