package net.ntworld.sentryIntegrationIdea.serviceProvider.community

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import net.ntworld.sentryIntegrationIdea.serviceProvider.ApplicationServiceProviderImpl

@State(name = "SentryIntegrationApplicationLevel", storages = [(Storage("sentry-integration.xml"))])
class ApplicationServiceProviderCommunityImpl: ApplicationServiceProviderImpl(), ApplicationServiceProviderCommunity {
    override val isPaidPlugin: Boolean = false

    override val toolWindowConfigurationGroup: String = "sentry.integration.community.toolWindow"
}