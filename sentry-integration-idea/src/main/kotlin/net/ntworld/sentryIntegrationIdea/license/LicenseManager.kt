package net.ntworld.sentryIntegrationIdea.license

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.notifier.LinkedProjectNotifier
import net.ntworld.sentryIntegrationIdea.panel.LicenseInfoPanel
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class LicenseManager(
    private val projectServiceProvider: ProjectServiceProvider,
    private val toolWindow: ToolWindow
) {
    private val messageBusConnection = projectServiceProvider.project.messageBus.connect()
    private val contentComponent = LicenseInfoPanel(projectServiceProvider.applicationServiceProvider.paidPluginUrl)
    private val myContent by lazy {
        val content = ContentFactory.SERVICE.getInstance().createContent(
            contentComponent.component,
            "License Required",
            true
        )
        content.isCloseable = false
        content
    }
    private val myLinkedProjectNotifier = object : LinkedProjectNotifier {
        override fun linkedProjectsChanged(linkedProjects: List<LinkedProject>) {
            if (projectServiceProvider.isLicenced()) {
                open()
            } else {
                close()
            }
        }
    }

    var isOpen = false
        private set

    init {
        messageBusConnection.subscribe(LinkedProjectNotifier.TOPIC, myLinkedProjectNotifier)
    }

    fun open() {
        isOpen = true
        ApplicationManager.getApplication().invokeLater {
            toolWindow.contentManager.removeContent(myContent, false)
        }
    }

    fun close() {
        isOpen = false
        ApplicationManager.getApplication().invokeLater {
            toolWindow.contentManager.addContent(myContent)
        }
    }

    fun dispose() {
        messageBusConnection.disconnect()
        if (isOpen) {
            toolWindow.contentManager.removeContent(myContent, true)
        }
    }
}
