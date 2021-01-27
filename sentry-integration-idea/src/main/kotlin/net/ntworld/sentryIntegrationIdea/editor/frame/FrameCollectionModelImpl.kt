package net.ntworld.sentryIntegrationIdea.editor.frame

import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.AbstractModel

class FrameCollectionModelImpl(
    override val visibleLine: Int
): AbstractModel<FrameCollectionModel.DataListener>(), FrameCollectionModel {
    override val dispatcher = EventDispatcher.create(FrameCollectionModel.DataListener::class.java)
    private val myFrames = mutableMapOf<String, Storage.Frame>()

    override val frames: List<Storage.Frame> = myFrames.values.toList()
    override var displayedSingleFrame: Storage.Frame? = null

    override fun isDisplayingSingleFrame(): Boolean = null !== displayedSingleFrame

    override fun addFrame(frame: Storage.Frame) {
        myFrames[frame.id] = frame
    }
}