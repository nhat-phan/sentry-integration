package net.ntworld.sentryIntegrationIdea.compatibility

import com.intellij.openapi.editor.impl.EditorEmbeddedComponentManager
import com.intellij.openapi.project.Project

interface IntellijIdeApi {
    fun notifySetupConnection(project: Project, toolWindowConfigurationGroup: String)

    fun makeEditorEmbeddedComponentManagerProperties(offset: Int): EditorEmbeddedComponentManager.Properties
}