package net.ntworld.sentryIntegrationIdea.setupWizard

import com.intellij.openapi.Disposable
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.LocalRepository
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.View
import java.util.*

interface SetupWizardView: View<SetupWizardView.ActionListener>, Component, Disposable {
    fun displayCreateConnectionForm(showCancelButton: Boolean)

    fun displaySelectConnectionStep(connections: List<Connection>, selectedConnectionId: String?)

    fun displaySelectProjectStep(repositories: List<LocalRepository>, linkedProjects: List<LinkedProject>)

    fun displayLoadingStateInFillEnvironmentForm()

    fun displayFillEnvironmentInformationStep(projects: List<SentryProject>, linkedProjects: List<LinkedProject>)

    interface ActionListener : EventListener {
        fun onCreateConnectionButtonClicked()

        fun onCreateConnectionCancelClicked()

        fun onConnectionTested(connection: Connection)

        fun onConnectionSelected(connection: Connection)

        fun onProjectSelected(localRootPath: String, name: String)

        fun onEnvironmentFilled(
            environmentName: String,
            sentryProject: SentryProject,
            deployedBranch: String,
            deployedRootPath: String,
            useCompiledLanguage: Boolean
        )
    }
}