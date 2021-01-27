package net.ntworld.sentryIntegrationIdea.compatibility

import com.intellij.openapi.editor.impl.EditorEmbeddedComponentManager

interface IntellijIdeApi {
    fun makeEditorEmbeddedComponentManagerProperties(offset: Int): EditorEmbeddedComponentManager.Properties
}