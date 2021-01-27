package net.ntworld.sentryIntegrationIdea.editor.frame

import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.Model
import java.util.*

interface FrameCollectionModel : Model<FrameCollectionModel.DataListener> {
    val visibleLine: Int

    val frames: List<Storage.Frame>

    var displayedSingleFrame: Storage.Frame?

    fun isDisplayingSingleFrame(): Boolean

    fun addFrame(frame: Storage.Frame)

    interface DataListener : EventListener {

    }
}