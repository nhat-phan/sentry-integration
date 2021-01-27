package net.ntworld.sentryIntegrationIdea.serviceProvider.community

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import net.ntworld.sentryIntegrationIdea.serviceProvider.PluginConfigurable
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class PluginConfigurableCommunity(private val project: Project): PluginConfigurable(project)
{
    override fun getProjectServiceProvider(): ProjectServiceProvider {
        return ServiceManager.getService(
            project,
            ProjectServiceProviderCommunity::class.java
        )
    }
}