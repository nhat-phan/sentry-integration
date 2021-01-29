package net.ntworld.sentryIntegrationIdea.setupWizard.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.panels.Wrapper
import com.intellij.util.EventDispatcher
import com.intellij.util.ui.UIUtil
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.LocalRepository
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField
import net.ntworld.sentryIntegration.toCrossPlatformsPath
import net.ntworld.sentryIntegrationIdea.setupWizard.SetupWizardView

class SelectProjectComponent(
    private val dispatcher: EventDispatcher<SetupWizardView.ActionListener>
): AbstractWizardStep("STEP 2/3: SELECT PROJECT") {
    private val myRepositories = ComboBox<String>()
    private val myProjectName = JTextField()
    private val myNextButton = JButton()
    private val myCurrentProjects = mutableMapOf<String, String>()

    init {
        val myWrapper = JPanel(MigLayout("wrap, insets 0", "[right]5[fill,grow]", "10[center]10"))
        myWrapper.background = UIUtil.getEditorPaneBackground()

        myWrapper.add(makeLabel("Local Repository"))
        val myComboBaxWrapper = Wrapper(myRepositories)
        myComboBaxWrapper.setHorizontalSizeReferent(myProjectName)
        myComboBaxWrapper.background = UIUtil.getEditorPaneBackground()
        myWrapper.add(myComboBaxWrapper)
        myRepositories.addActionListener { this.onRepositoryChanged() }

        myWrapper.add(makeLabel("Project Name"))
        myWrapper.add(myProjectName)

        component.add(myWrapper, "wrap")

        val myButtonWrapper = JPanel(MigLayout("wrap, insets 0", "10push[]10[]10", "10[center]10"))
        myNextButton.text = "Next"
        myNextButton.background = UIUtil.getEditorPaneBackground()
        myButtonWrapper.add(myNextButton)
        myButtonWrapper.background = UIUtil.getEditorPaneBackground()

        myNextButton.addActionListener { this.validateAndMoveToNextStep() }
        component.add(myButtonWrapper, "dock south")
    }

    override fun onStateChanged() {
        myRepositories.isEnabled = isActive
        myProjectName.isEnabled = isActive
        myNextButton.isEnabled = isActive
    }

    private fun onRepositoryChanged() {
        val path = myRepositories.selectedItem
        if (null !== path && path is String) {
            val currentProject = myCurrentProjects[path]
            if (null !== currentProject) {
                myProjectName.text = currentProject
                if (myRepositories.itemCount == 1) {
                    return this.validateAndMoveToNextStep()
                }
            }

            val crossPlatformPath = path.toCrossPlatformsPath()
            val lastIndex = crossPlatformPath.lastIndexOf('/')
            if (lastIndex > 0 && lastIndex < crossPlatformPath.length) {
                myProjectName.text = crossPlatformPath.substring(lastIndex + 1)
                myProjectName.requestFocus()
            }
        }
    }

    private fun validateAndMoveToNextStep() {
        val path = myRepositories.selectedItem
        val name = myProjectName.text.trim()
        if (null !== path && path is String && path.trim().isNotEmpty() && name.isNotEmpty()) {
            dispatcher.multicaster.onProjectSelected(path.trim(), name)
        }
    }

    fun setRepositories(repositories: List<LocalRepository>, linkedProjects: List<LinkedProject>) {
        myRepositories.removeAllItems()
        for (repository in repositories) {
            myRepositories.addItem(repository.path)
        }

        myCurrentProjects.clear()
        for (linkedProject in linkedProjects) {
            if (linkedProject.state == LinkedProject.State.READY) {
                myCurrentProjects[linkedProject.localRootPath] = linkedProject.name
            }
        }

        myRepositories.selectedItem = repositories.first().path
        onRepositoryChanged()
    }
}