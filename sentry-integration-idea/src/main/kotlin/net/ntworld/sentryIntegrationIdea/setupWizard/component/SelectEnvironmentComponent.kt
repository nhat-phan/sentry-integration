package net.ntworld.sentryIntegrationIdea.setupWizard.component

import com.intellij.util.EventDispatcher
import com.intellij.util.ui.UIUtil
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.component.EnvironmentFormComponent
import net.ntworld.sentryIntegrationIdea.setupWizard.SetupWizardView
import javax.swing.JButton
import javax.swing.JPanel

class SelectEnvironmentComponent(
    val dispatcher: EventDispatcher<SetupWizardView.ActionListener>
): AbstractWizardStep("STEP 3/3: ENVIRONMENT INFORMATION") {
    private val myEnvironmentFormComponent = EnvironmentFormComponent()
    private val myFinishButton = JButton()
    private val myPredefineEnvironmentNames = listOf("production", "worker", "stage", "qa", "dev")

    init {
        component.add(myEnvironmentFormComponent.component, "wrap")
        myEnvironmentFormComponent.changeBackgroundToEditorPaneBackground()

        val myButtonWrapper = JPanel(MigLayout("wrap, insets 0", "10push[]10[]10", "10[center]10"))
        myFinishButton.text = "Finish"
        myButtonWrapper.add(myFinishButton)
        myFinishButton.background = UIUtil.getEditorPaneBackground()
        myButtonWrapper.background = UIUtil.getEditorPaneBackground()
        myFinishButton.addActionListener { this.validateAndMoveToNextStep() }

        component.add(myButtonWrapper, "dock south")
    }

    override fun onStateChanged() {
        myEnvironmentFormComponent.setState(isActive)
        myFinishButton.isEnabled = isActive
    }

    fun setProjects(projects: List<SentryProject>, linkedProjects: List<LinkedProject>) {
        myEnvironmentFormComponent.setProjects(projects)

        myEnvironmentFormComponent.setEnvironmentName(guessNextName(linkedProjects))
        myEnvironmentFormComponent.setDeployedBranch(guessDeployedBranch(linkedProjects))
        myEnvironmentFormComponent.setDeployedRootPath(guessDeployedRootPath(linkedProjects))
        if (projects.isNotEmpty()) {
            myEnvironmentFormComponent.setSelectedProject(projects.first().id)
        }
        myEnvironmentFormComponent.requestFocusOnEnvironmentName()
    }

    private fun validateAndMoveToNextStep() {
        val name = myEnvironmentFormComponent.getEnvironmentName().trim()
        val project = myEnvironmentFormComponent.getSelectedProject()
        if (null !== project) {
            dispatcher.multicaster.onEnvironmentFilled(
                name,
                project,
                myEnvironmentFormComponent.getDeployedBranch(),
                myEnvironmentFormComponent.getDeployedRootPath(),
                myEnvironmentFormComponent.getUseCompiledLanguage()
            )
        }
    }

    private fun guessNextName(linkedProjects: List<LinkedProject>): String {
        for (predefinedName in myPredefineEnvironmentNames) {
            var hasName = false
            for (linkedProject in linkedProjects) {
                val name = linkedProject.environmentName.toLowerCase()
                if (name.indexOf(predefinedName) != -1) {
                    hasName = true
                    break
                }
            }
            if (!hasName) {
                return predefinedName
            }
        }
        return ""
    }

    private fun guessDeployedBranch(linkedProjects: List<LinkedProject>): String {
        for (linkedProject in linkedProjects) {
            if (linkedProject.deployedBranch.isNotEmpty()) {
                return linkedProject.deployedBranch
            }
        }
        return "origin/master"
    }

    private fun guessDeployedRootPath(linkedProjects: List<LinkedProject>): String {
        for (linkedProject in linkedProjects) {
            if (linkedProject.sentryRootPath.isNotEmpty()) {
                return linkedProject.sentryRootPath
            }
        }
        return ""
    }
}