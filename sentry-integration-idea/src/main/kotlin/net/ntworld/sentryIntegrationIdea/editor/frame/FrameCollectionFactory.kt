package net.ntworld.sentryIntegrationIdea.editor.frame

import com.intellij.openapi.editor.ex.EditorEx
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

object FrameCollectionFactory {
    fun makePresenter(
        projectServiceProvider: ProjectServiceProvider,
        editor: EditorEx,
        visibleLine: Int
    ): FrameCollectionPresenter {
        val model = FrameCollectionModelImpl(visibleLine)
        val view = FrameCollectionViewImpl(
            projectServiceProvider,
            editor,
            visibleLine
        )

        return FrameCollectionPresenterImpl(projectServiceProvider, model = model, view = view)
    }
}