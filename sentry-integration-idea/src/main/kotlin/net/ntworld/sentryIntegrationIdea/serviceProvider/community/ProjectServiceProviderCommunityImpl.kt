package net.ntworld.sentryIntegrationIdea.serviceProvider.community

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import net.ntworld.sentryIntegrationIdea.serviceProvider.ApplicationServiceProvider
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProviderImpl

@State(name = "SentryIntegrationProjectLevel", storages = [(Storage("sentry-integration.xml"))])
class ProjectServiceProviderCommunityImpl(project: Project): ProjectServiceProviderImpl(project), ProjectServiceProviderCommunity {
    override val applicationServiceProvider: ApplicationServiceProvider = ServiceManager.getService(
        ApplicationServiceProviderCommunity::class.java
    )
}