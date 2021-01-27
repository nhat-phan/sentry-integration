package net.ntworld.sentryIntegrationIdea.component

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.panels.Wrapper
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.Component
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class EnvironmentFormComponent(displayProjectName: Boolean = false): Component {
    override val component: JComponent = JPanel(MigLayout("wrap, insets 0", "[right]5[fill,grow]", "5[center]5"))
    private val myProjectName = JTextField()
    private val myEnvironmentName = JTextField()
    private val myEnableWorkerCheckbox = JCheckBox()
    private val myProjects = ComboBox<Item>()
    private val myDeployedBranch = JTextField()
    private val myDeployedRootPath = JTextField()

    init {
        if (displayProjectName) {
            component.add(makeLabel("Project Name"))
            component.add(myProjectName)
        }

        component.add(makeLabel("Environment Name"))
        component.add(myEnvironmentName)

        myEnableWorkerCheckbox.isEnabled = false
        myEnableWorkerCheckbox.text = "Enable worker fetches and displays issues in editor. (coming soon)"
        component.add(makeEmptyComponent())
        component.add(myEnableWorkerCheckbox)

        component.add(makeLabel("Sentry Project"))
        val myComboBaxWrapper = Wrapper(myProjects)
        myComboBaxWrapper.setHorizontalSizeReferent(myEnvironmentName)
        component.add(myComboBaxWrapper)

        component.add(makeLabel("Deployed Branch"))
        component.add(myDeployedBranch)

        component.add(makeLabel("Deployed Root Path"))
        component.add(myDeployedRootPath)
        component.add(makeEmptyComponent())
        component.add(makeLabel("<html>Root Path is the directory which your code was deployed.<br />The plugin uses Root Path to map your code with local and jumps between stacktrace </html>"))
    }

    fun setState(isActive: Boolean) {
        myEnvironmentName.isEnabled = isActive
        myProjects.isEnabled = isActive
        myDeployedBranch.isEnabled = isActive
        myDeployedRootPath.isEnabled = isActive
    }

    fun requestFocusOnEnvironmentName() {
        myEnvironmentName.requestFocus()
    }

    fun getProjectName(): String = myProjectName.text
    fun setProjectName(value: String) { myProjectName.text = value}

    fun getEnvironmentName(): String = myEnvironmentName.text
    fun setEnvironmentName(value: String) { myEnvironmentName.text = value}

    fun getDeployedBranch(): String = myDeployedBranch.text
    fun setDeployedBranch(value: String) { myDeployedBranch.text = value}

    fun getDeployedRootPath(): String = myDeployedRootPath.text
    fun setDeployedRootPath(value: String) { myDeployedRootPath.text = value}

    fun getSelectedProject(): SentryProject? {
        val item = myProjects.selectedItem
        if (null === item || item !is Item) {
            return null
        }
        return item.sentryProject
    }

    fun setSelectedProject(id: String) {
        val count = myProjects.itemCount
        for (i in 0 until count) {
            val item = myProjects.getItemAt(i)
            if (item.id == id) {
                myProjects.selectedItem = item
                break
            }
        }
    }

    fun setProjects(projects: List<SentryProject>) {
        myProjects.removeAllItems()
        for (project in projects) {
            myProjects.addItem(Item(id = project.id, sentryProject = project))
        }
    }

    private fun makeLabel(text: String): JLabel {
        val label = JLabel()
        label.text = text
        return label
    }

    private fun makeEmptyComponent() = JLabel()

    private class Item(val id: String, val sentryProject: SentryProject) {
        override fun toString(): String {
            return sentryProject.name + " - " + sentryProject.slug
        }

        override fun equals(other: Any?): Boolean {
            if (null === other || other !is Item) {
                return false
            }
            return other.id == id
        }
    }
}