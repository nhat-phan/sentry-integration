package net.ntworld.sentryIntegrationIdea.compatibility

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.openapi.editor.impl.EditorEmbeddedComponentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType

class Version201Adapter: IntellijIdeApi {
    override fun notifySetupConnection(project: Project, toolWindowConfigurationGroup: String) {
        val notificationGroup = NotificationGroup(
            toolWindowConfigurationGroup, NotificationDisplayType.TOOL_WINDOW, false, toolWindowConfigurationGroup
        )
        val notification = notificationGroup.createNotification(
            "Thank you for using the plugin, please click \"Sentry Integration\" to start.",
            MessageType.INFO
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