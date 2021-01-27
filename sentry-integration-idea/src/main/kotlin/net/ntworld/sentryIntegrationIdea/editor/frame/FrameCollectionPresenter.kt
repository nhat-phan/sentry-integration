package net.ntworld.sentryIntegrationIdea.editor.frame

import com.intellij.openapi.Disposable
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.Presenter
import net.ntworld.sentryIntegrationIdea.editor.gutter.GutterIconRenderer

interface FrameCollectionPresenter: Presenter<FrameCollectionPresenter.EventListener>, Disposable {
    fun add(frame: Storage.Frame)

    fun hideAll()

    fun displaySingleFrame(frame: Storage.Frame)

    fun handleGutterAction(gutterIconRenderer: GutterIconRenderer)

    interface EventListener: java.util.EventListener {

    }
}