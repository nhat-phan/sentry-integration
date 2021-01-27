package net.ntworld.sentryIntegrationIdea.serviceProvider

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import net.ntworld.sentryIntegrationIdea.panel.ConfigurationPanel
import javax.swing.JComponent

open class PluginConfigurable(
    private val project: Project
): Configurable {
    private val myConfigurationPanel by lazy { ConfigurationPanel() }

    protected open fun getProjectServiceProvider(): ProjectServiceProvider {
        return ServiceManager.getService(
            project,
            ProjectServiceProvider::class.java
        )
    }

    override fun createComponent(): JComponent? {
        myConfigurationPanel.setPluginConfiguration(getProjectServiceProvider().pluginConfiguration)

        return myConfigurationPanel.myWrapper
    }

    override fun isModified(): Boolean {
        return getProjectServiceProvider().pluginConfiguration != myConfigurationPanel.getPluginConfiguration()
    }

    override fun apply() {
        getProjectServiceProvider().applyPluginConfiguration(myConfigurationPanel.getPluginConfiguration())
    }

    override fun getDisplayName(): String = getProjectServiceProvider().configurableDisplayName
}