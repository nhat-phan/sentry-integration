package net.ntworld.sentryIntegrationIdea.toolWindow

import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.ComponentFactory
import net.ntworld.sentryIntegrationIdea.component.ProjectsTreeComponent
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class OpenProjectsDialog(
    projectServiceProvider: ProjectServiceProvider,
    private val linkedProjects: List<LinkedProject>
) {
    private val myProjectTreeComponent = ComponentFactory.makeProjectsTreeComponent(projectServiceProvider)
    private var mySelectedProjects: List<LinkedProject>? = null
    private var mySelectedProject: LinkedProject? = null
    private val myProjectTreeComponentListener = object : ProjectsTreeComponent.Listener {
        override fun onProjectTreeUnselected() {
            mySelectedProjects = null
            mySelectedProject = null
        }

        override fun onProjectSelected(projects: List<LinkedProject>, isSelectConnection: Boolean) {
            mySelectedProjects = projects
        }

        override fun onEnvironmentSelected(project: LinkedProject) {
            mySelectedProject = project
        }
    }

    init {
        myProjectTreeComponent.addActionListener(myProjectTreeComponentListener)
    }

    fun openDialog(): List<LinkedProject> {
        myProjectTreeComponent.setLinkedProjects(linkedProjects)
        myProjectTreeComponent.expandAll()

        mySelectedProjects = null
        mySelectedProject = null

        val builder = DialogBuilder()
        builder.setCenterPanel(myProjectTreeComponent.component)

        builder.removeAllActions()
        builder.addOkAction()
        val exitCode = builder.show()
        if (exitCode == DialogWrapper.OK_EXIT_CODE) {
            val projects = mySelectedProjects
            if (null !== projects) {
                return projects
            }

            val project = mySelectedProject
            if (null !== project) {
                return listOf(project)
            }
        }
        return listOf()
    }
}