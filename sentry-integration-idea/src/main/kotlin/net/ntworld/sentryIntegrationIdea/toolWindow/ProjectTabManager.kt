package net.ntworld.sentryIntegrationIdea.toolWindow

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import net.ntworld.sentryIntegration.debug
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.projectTab.ProjectTabFactory
import net.ntworld.sentryIntegrationIdea.projectTab.ProjectTabPresenter
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.task.ValidateLinkedProjectsTask
import net.ntworld.sentryIntegrationIdea.util.ModelUtil

class ProjectTabManager(
    private val projectServiceProvider: ProjectServiceProvider,
    private val toolWindow: ToolWindow
) {
    // First release will always open linked projects
    private val MAX_PROJECT_OPENED_BY_DEFAULT = 3
    private val myTabs = mutableMapOf<String, ToolWindowTab<ProjectTabPresenter>>()
    private val myLinkedProjects = mutableMapOf<String, LinkedProject>()
    private val myReopenIds = mutableSetOf<String>()
    private val myValidateLinkedProjectsTaskListener = object : ValidateLinkedProjectsTask.Listener {
        override fun onLinkedProjectValidated(validated: List<LinkedProject>) {
            projectServiceProvider.updateLinkedProjectsState(validated)

            val readyProjects = validated.filter { it.state == LinkedProject.State.READY }
            val openingList = projectServiceProvider.getOpeningProjectIds()
            if (openingList.isEmpty()) {
                if (readyProjects.count() <= MAX_PROJECT_OPENED_BY_DEFAULT) {
                    ApplicationManager.getApplication().invokeLater {
                        doOpenLinkedProjects(readyProjects)
                    }
                }
            } else {
                for (readyProject in readyProjects) {
                    if (openingList.contains(readyProject.id)) {
                        doOpenLinkedProject(readyProject)
                    }
                }
            }

            for (reopenId in myReopenIds) {
                val project = readyProjects.firstOrNull { it.id == reopenId }
                if (null !== project) {
                    doOpenLinkedProject(project)
                }
            }
            myReopenIds.clear()
        }
    }
    private val myTabListener = object: ToolWindowTab.Listener {
        override fun didOpen(component: Any, willBeDisposed: Boolean) {
            if (component !is ProjectTabPresenter) {
                return
            }
            projectServiceProvider.setLinkedProjectAsOpened(component.linkedProject.id)
        }

        override fun didClose(component: Any, willBeDisposed: Boolean) {
            if (component !is ProjectTabPresenter) {
                return
            }
            projectServiceProvider.setLinkedProjectAsClosed(component.linkedProject.id)
        }
    }

    fun hasAnyProjectReadyToOpen(): Boolean {
        for (item in myLinkedProjects) {
            if (item.value.state != LinkedProject.State.READY) {
                continue
            }
            val tab = myTabs[item.key]
            if (null === tab || !tab.isOpened) {
                return true
            }
        }
        return false
    }

    fun setLinkedProjects(projects: List<LinkedProject>) {
        if (ModelUtil.isAnyProjectChangedInMap(projects, myLinkedProjects)) {
            ModelUtil.copyProjectsToMap(projects, myLinkedProjects)
            for (tab in myTabs) {
                if (!tab.value.isOpened) {
                    continue
                }
                val current = tab.value.component.linkedProject
                val new = myLinkedProjects[current.id]
                if (null === new) {
                    ApplicationManager.getApplication().invokeLater { tab.value.close() }
                    continue
                }
                if (new != current) {
                    tab.value.updateTabName("${new.name} · ${new.environmentName}")
                    myReopenIds.add(current.id)
                    ApplicationManager.getApplication().invokeLater {
                        tab.value.close()
                    }
                }
            }
            validateProjects()
        }
    }

    fun openLinkedProjects(projects: List<LinkedProject>) {
        for (project in projects) {
            if (project.state != LinkedProject.State.READY) {
                continue
            }

            doOpenLinkedProject(project)
        }
    }

    private fun doOpenLinkedProjects(projects: List<LinkedProject>) {
        for (project in projects) {
            doOpenLinkedProject(project)
        }
    }

    private fun doOpenLinkedProject(linkedProject: LinkedProject) {
        val tab = myTabs[linkedProject.id]
        if (null !== tab) {
            ApplicationManager.getApplication().invokeLater { tab.open() }
            return
        }

        val created = ToolWindowTab(
            toolWindow,
            ToolWindowTab.Properties(
                tabName = "${linkedProject.name} · ${linkedProject.environmentName}",
                disposeAfterRemoved = true,
                listener = myTabListener
            )
        ) { ProjectTabFactory.makeProjectTabPresenter(projectServiceProvider, linkedProject) }

        myTabs[linkedProject.id] = created
        ApplicationManager.getApplication().invokeLater { created.open() }
    }

    private fun validateProjects() {
        val initializeProjects = myLinkedProjects.values.toList().filter {
            it.state == LinkedProject.State.INITIALIZE ||
            it.state == LinkedProject.State.INVALID_LOCAL_ROOT_PATH
        }
        if (initializeProjects.isEmpty()) {
            return
        }

        ValidateLinkedProjectsTask(
            projectServiceProvider,
            initializeProjects,
            myValidateLinkedProjectsTaskListener
        ).start("MainToolWindowLinkedProjectContentManager")
    }
}