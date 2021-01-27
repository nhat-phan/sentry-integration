package net.ntworld.sentryIntegrationIdea.setupWizard

import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

object SetupWizardFactory {

    fun makeSetupWizard(projectServiceProvider: ProjectServiceProvider): SetupWizardPresenter {
        val model = SetupWizardModelImpl()
        val view = SetupWizardViewImpl()

        return SetupWizardPresenterImpl(projectServiceProvider, model, view)
    }

}