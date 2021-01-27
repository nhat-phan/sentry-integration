package net.ntworld.sentryIntegrationIdea.toolWindow.community

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import net.ntworld.sentryIntegrationIdea.serviceProvider.ApplicationServiceProvider
import net.ntworld.sentryIntegrationIdea.serviceProvider.community.ApplicationServiceProviderCommunity
import net.ntworld.sentryIntegrationIdea.serviceProvider.community.ProjectServiceProviderCommunity
import net.ntworld.sentryIntegrationIdea.toolWindow.MainToolWindowManagerImpl

class MainToolWindowFactoryCommunity : ToolWindowFactory {
    // It is required to initialize our ApplicationServiceProvider and register watchers
    val applicationServiceProvider: ApplicationServiceProvider = ServiceManager.getService(ApplicationServiceProviderCommunity::class.java)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val projectServiceProvider = ServiceManager.getService(
            project,
            ProjectServiceProviderCommunity::class.java
        )
        MainToolWindowManagerImpl(projectServiceProvider, toolWindow)
    }
}