package net.ntworld.sentryIntegrationIdea.toolWindow

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import net.ntworld.sentryIntegrationIdea.serviceProvider.ApplicationServiceProvider
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class MainToolWindowFactory : ToolWindowFactory {
    // It is required to initialize our ApplicationServiceProvider and register watchers
    val applicationServiceProvider: ApplicationServiceProvider = ServiceManager.getService(ApplicationServiceProvider::class.java)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val projectServiceProvider = ServiceManager.getService(
            project,
            ProjectServiceProvider::class.java
        )
        MainToolWindowManagerImpl(projectServiceProvider, toolWindow)
    }
}