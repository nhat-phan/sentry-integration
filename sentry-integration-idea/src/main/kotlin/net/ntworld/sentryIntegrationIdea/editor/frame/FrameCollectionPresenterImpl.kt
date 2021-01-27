package net.ntworld.sentryIntegrationIdea.editor.frame

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.StorageManager
import net.ntworld.sentryIntegrationIdea.AbstractPresenter
import net.ntworld.sentryIntegrationIdea.editor.gutter.GutterIconRenderer
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import java.awt.datatransfer.StringSelection

class FrameCollectionPresenterImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val model: FrameCollectionModel,
    private val view: FrameCollectionView
) : AbstractPresenter<FrameCollectionPresenter.EventListener>(),
    FrameCollectionPresenter, FrameCollectionModel.DataListener, FrameCollectionView.ActionListener {
    override val dispatcher = EventDispatcher.create(FrameCollectionPresenter.EventListener::class.java)

    init {
        model.addDataListener(this)
        view.addActionListener(this)
        for (frame in model.frames) {
            view.displaySingleFrame(frame)
        }
    }

    override fun add(frame: Storage.Frame) {
        model.addFrame(frame)
    }

    override fun hideAll() {
        view.hideAll()
    }

    override fun displaySingleFrame(frame: Storage.Frame) {
        model.displayedSingleFrame = frame
        view.displaySingleFrame(frame)
    }

    override fun handleGutterAction(gutterIconRenderer: GutterIconRenderer) {
        if (view.isDisplayAnyFrame()) {
            view.hideAll()
        } else {
            if (model.isDisplayingSingleFrame()) {
                val frame = model.displayedSingleFrame
                if (null !== frame) {
                    view.displaySingleFrame(frame)
                }
            }
        }
    }

    override fun dispose() {
        view.dispose()
    }

    override fun onJumpBackwardClicked(frame: Storage.Frame) = findAndOpenEditorByFrameLink(frame, frame.previous)

    override fun onJumpForwardClicked(frame: Storage.Frame) = findAndOpenEditorByFrameLink(frame, frame.next)

    override fun onJumpToTopClicked(frame: Storage.Frame) {
        val storage = StorageManager.make(frame.linkedProject)
        val firstFrame = storage.findFirstFrame(frame)
        if (null !== firstFrame) {
            projectServiceProvider.editorManager.open(firstFrame)
        }
    }

    override fun onOpenInBrowserClicked(frame: Storage.Frame) {
        val storage = StorageManager.make(frame.linkedProject)
        val issue = storage.findIssueById(frame.issueId)
        if (null !== issue) {
            BrowserUtil.open(issue.permalink)
        }
    }

    override fun onCopyIssueLinkClicked(frame: Storage.Frame) {
        val storage = StorageManager.make(frame.linkedProject)
        val issue = storage.findIssueById(frame.issueId)
        if (null !== issue) {
            CopyPasteManager.getInstance().setContents(StringSelection(issue.permalink))
        }
    }

    private fun findAndOpenEditorByFrameLink(frame: Storage.Frame, link: Storage.FrameLink?) {
        if (null === link) {
            return
        }

        val storage = StorageManager.make(frame.linkedProject)
        val nextFrame = storage.findFrameByLink(link, frame.source)
        if (null !== nextFrame) {
            projectServiceProvider.editorManager.open(nextFrame)
        }
    }
}