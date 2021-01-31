package net.ntworld.sentryIntegrationIdea.compatibility

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.impl.EditorEmbeddedComponentManager
import com.intellij.openapi.project.Project

class Version203Adapter : IntellijIdeApi {
    override fun notifySetupConnection(project: Project, toolWindowConfigurationGroup: String) {
        val notificationGroupManager = ApplicationManager.getApplication().getService(NotificationGroupManager::class.java)
        val notificationGroup = notificationGroupManager.getNotificationGroup(toolWindowConfigurationGroup)
        val notification = notificationGroup.createNotification(
            "Thank you for using the plugin, please click here to start",
            NotificationType.INFORMATION
        )
        notification.notify(project)
    }

    override fun makeEditorEmbeddedComponentManagerProperties(offset: Int): EditorEmbeddedComponentManager.Properties {
        return EditorEmbeddedComponentManager.Properties(
            EditorEmbeddedComponentManager.ResizePolicy.none(),
            null,
            true,
            false,
            0,
            offset
        )
    }
}