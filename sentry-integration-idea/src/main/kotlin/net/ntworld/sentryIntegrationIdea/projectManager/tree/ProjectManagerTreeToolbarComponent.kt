package net.ntworld.sentryIntegrationIdea.projectManager.tree

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.util.EventDispatcher
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.Icons
import net.ntworld.sentryIntegrationIdea.projectManager.ProjectManagerView
import javax.swing.JComponent
import javax.swing.JPanel

class ProjectManagerTreeToolbarComponent(
    private val dispatcher: EventDispatcher<ProjectManagerView.ActionListener>,
    private val wrapper: ProjectManagerTreeWrapperComponent
): Component {
    private val myAddProjectAction = MyAddProjectAction(this)
    private val myEditProjectAction = MyEditProjectAction(this)
    private val myEditEnvironmentAction = MyEditEnvironmentAction(this)
    private val myDeleteConnectionAction = MyDeleteConnectionAction(this)
    private val myDeleteProjectAction = MyDeleteProjectAction(this)
    private val myDeleteEnvironmentAction = MyDeleteEnvironmentAction(this)
    private val myOpenAction = MyOpenAction(this)
    private val myComponent by lazy {
        val panel = JPanel(MigLayout("ins 0, fill", "5[left]push[right]", "center"))
        val leftActionGroup = DefaultActionGroup()
        leftActionGroup.add(myAddProjectAction)
        val leftToolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar-left",
            leftActionGroup,
            true
        )

        val rightActionGroup = DefaultActionGroup()
        rightActionGroup.add(myOpenAction)
        rightActionGroup.addSeparator()
        rightActionGroup.add(myEditProjectAction)
        rightActionGroup.add(myEditEnvironmentAction)
        rightActionGroup.add(myDeleteConnectionAction)
        rightActionGroup.add(myDeleteProjectAction)
        rightActionGroup.add(myDeleteEnvironmentAction)
        val rightToolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar-right",
            rightActionGroup,
            true
        )

        panel.add(leftToolbar.component)
        panel.add(rightToolbar.component)
        panel
    }

    override val component: JComponent = myComponent

    private fun isSelectedProjectHasOnlyOneEnvironment(): Boolean
    {
        val selectedProjectCollection = wrapper.selectedProjectCollection
        return null !== selectedProjectCollection && selectedProjectCollection.count() == 1
    }

    private fun isConnectionSelected(): Boolean {
        return null !== wrapper.selectedConnection
    }

    private fun isProjectSelected(): Boolean {
        if (null !== wrapper.selectedConnection) {
            return false
        }
        val selectedProjectCollection = wrapper.selectedProjectCollection
        return null !== selectedProjectCollection
    }

    private fun isEnvironmentSelected(): Boolean {
        if (null !== wrapper.selectedConnection) {
            return false
        }
        val selectedProject = wrapper.selectedProject
        return null !== selectedProject
    }

    private class MyAddProjectAction(private val self: ProjectManagerTreeToolbarComponent) : AnAction(
        "New Project", "Add new project", AllIcons.General.Add
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.dispatcher.multicaster.onAddProjectClicked()
        }

        override fun displayTextInToolbar(): Boolean = true
    }

    private class MyOpenAction(private val self: ProjectManagerTreeToolbarComponent) : AnAction(
        "Open", null, AllIcons.Actions.MenuOpen
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val projects = self.wrapper.selectedProjectCollection
            if (null !== projects) {
                self.dispatcher.multicaster.onOpenProjectsClicked(projects)
            }
        }

        override fun update(e: AnActionEvent) {
            val projects = self.wrapper.selectedProjectCollection
            val project = self.wrapper.selectedProject
            e.presentation.isEnabled =
                (null !== projects && projects.count { it.state == LinkedProject.State.READY } > 0) ||
                (null !== project && project.state == LinkedProject.State.READY)
        }
    }

    private class MyEditProjectAction(private val self: ProjectManagerTreeToolbarComponent) : AnAction(
        "Rename Project", "Rename selected project", AllIcons.Actions.Edit
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val projects = self.wrapper.selectedProjectCollection
            if (null !== projects) {
                if (projects.count() == 1) {
                    self.dispatcher.multicaster.onEnvironmentEditRequest(projects.first(), true)
                } else {
                    self.dispatcher.multicaster.onProjectRenameClicked(projects)
                }
            }
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isVisible = self.isProjectSelected()
            if (self.isSelectedProjectHasOnlyOneEnvironment()) {
                e.presentation.text = "Edit Project"
                e.presentation.description = "Edit selected project"
            } else {
                e.presentation.text = "Rename Project"
                e.presentation.description = "Rename selected project"
            }
        }
    }

    private class MyEditEnvironmentAction(private val self: ProjectManagerTreeToolbarComponent) : AnAction(
        "Edit Environment", "Edit environment", AllIcons.Actions.Edit
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val project = self.wrapper.selectedProject
            if (null !== project) {
                self.dispatcher.multicaster.onEnvironmentEditRequest(project, false)
            }
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isVisible = self.isEnvironmentSelected()
        }
    }

    private class MyDeleteConnectionAction(private val self: ProjectManagerTreeToolbarComponent) : AnAction(
        "Delete Connection", "Delete selected connection and all projects", Icons.Trash
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val connection = self.wrapper.selectedConnection
            val projects = self.wrapper.selectedProjectCollection
            if (null !== connection && null !== projects) {
                self.dispatcher.multicaster.onConnectionDeleteRequest(connection, projects)
            }
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isVisible = self.isConnectionSelected()
        }
    }

    private class MyDeleteProjectAction(private val self: ProjectManagerTreeToolbarComponent) : AnAction(
        "Delete Project", "Delete selected project", AllIcons.General.Remove
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val projects = self.wrapper.selectedProjectCollection
            if (null !== projects) {
                self.dispatcher.multicaster.onProjectDeleteRequest(projects)
            }
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isVisible = self.isProjectSelected()
        }
    }

    private class MyDeleteEnvironmentAction(private val self: ProjectManagerTreeToolbarComponent) : AnAction(
        "Delete Environment", "Delete selected environment", AllIcons.General.Remove
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val project = self.wrapper.selectedProject
            if (null !== project) {
                self.dispatcher.multicaster.onEnvironmentDeleteRequest(project)
            }
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isVisible = self.isEnvironmentSelected()
        }
    }
}