package net.ntworld.sentryIntegrationIdea.projectManager.tree

import com.intellij.ui.ScrollPaneFactory
import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.ComponentFactory
import net.ntworld.sentryIntegrationIdea.CustomSimpleToolWindowPanel
import net.ntworld.sentryIntegrationIdea.component.ProjectsTreeComponent
import net.ntworld.sentryIntegrationIdea.projectManager.ProjectManagerView
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import javax.swing.JComponent

class ProjectManagerTreeWrapperComponent(
    private val projectServiceProvider: ProjectServiceProvider,
    private val dispatcher: EventDispatcher<ProjectManagerView.ActionListener>
): Component {
    private val myComponent = CustomSimpleToolWindowPanel(vertical = true, borderless = false)
    private val myProjectTreeComponent = ComponentFactory.makeProjectsTreeComponent(projectServiceProvider)
    private val myProjectManagerTreeToolbarComponent = ProjectManagerTreeToolbarComponent(dispatcher, this)
    private val myProjectTreeListenerForward = object : ProjectsTreeComponent.Listener {
        override fun onProjectTreeUnselected() = dispatcher.multicaster.onProjectTreeUnselected()

        override fun onProjectSelected(projects: List<LinkedProject>, isSelectConnection: Boolean) {
            dispatcher.multicaster.onProjectSelected(projects, isSelectConnection)
        }

        override fun onEnvironmentSelected(project: LinkedProject) = dispatcher.multicaster.onEnvironmentSelected(project)
    }
    var selectedConnection: Connection? = null
    var selectedProjectCollection: List<LinkedProject>? = null
    var selectedProject: LinkedProject? = null

    override val component: JComponent = myComponent

    init {
        myComponent.setContent(
            ScrollPaneFactory.createScrollPane(myProjectTreeComponent.component, true)
        )
        myComponent.toolbar = myProjectManagerTreeToolbarComponent.component

        myProjectTreeComponent.addActionListener(myProjectTreeListenerForward)
    }

    fun setLinkedProjects(projects: List<LinkedProject>) = myProjectTreeComponent.setLinkedProjects(projects)
}