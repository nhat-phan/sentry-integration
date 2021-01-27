package net.ntworld.sentryIntegrationIdea.editor.frame

import com.intellij.openapi.Disposable
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.View
import java.util.*

interface FrameCollectionView : View<FrameCollectionView.ActionListener>, Disposable {

    fun isDisplayAnyFrame(): Boolean

    fun displaySingleFrame(frame: Storage.Frame)

    fun hideAll()

    interface ActionListener : EventListener {

        fun onJumpBackwardClicked(frame: Storage.Frame)

        fun onJumpForwardClicked(frame: Storage.Frame)

        fun onJumpToTopClicked(frame: Storage.Frame)

        fun onOpenInBrowserClicked(frame: Storage.Frame)

        fun onCopyIssueLinkClicked(frame: Storage.Frame)

    }
}