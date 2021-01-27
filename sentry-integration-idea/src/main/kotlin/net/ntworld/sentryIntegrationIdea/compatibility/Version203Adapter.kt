package net.ntworld.sentryIntegrationIdea.compatibility

import com.intellij.openapi.editor.impl.EditorEmbeddedComponentManager

class Version203Adapter : IntellijIdeApi {
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