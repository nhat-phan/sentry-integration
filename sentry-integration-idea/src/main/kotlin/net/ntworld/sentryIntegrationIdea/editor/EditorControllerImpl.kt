package net.ntworld.sentryIntegrationIdea.editor

import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.util.Disposer
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.editor.frame.FrameCollectionFactory
import net.ntworld.sentryIntegrationIdea.editor.frame.FrameCollectionPresenter
import net.ntworld.sentryIntegrationIdea.editor.gutter.GutterIconRenderer
import net.ntworld.sentryIntegrationIdea.editor.gutter.GutterIconRendererFactory
import net.ntworld.sentryIntegrationIdea.editor.gutter.GutterState
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class EditorControllerImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    override val textEditor: TextEditor,
    override val editor: EditorEx
) : EditorController {

    private val myGutterIconRenderers = mutableMapOf<Int, GutterIconRenderer>()
    private val myFrameCollectionPresenters = mutableMapOf<Int, FrameCollectionPresenter>()

    init {
        Disposer.register(textEditor, this)
    }

    override fun initializeLine(frame: Storage.Frame) {
        initializeGutterIconRenderer(frame.visibleLine) {}
        initializeFrameCollectionPresenter(frame.visibleLine) { it.add(frame) }
    }

    override fun hideAllFramesInWholeEditor() {
        for (presenter in myFrameCollectionPresenters) {
            presenter.value.hideAll()
        }
    }

    override fun scrollToLine(visibleLine: Int) {
        editor.scrollingModel.scrollTo(LogicalPosition(visibleLine - 1, 0), ScrollType.MAKE_VISIBLE)
    }

    override fun displaySingleFrame(frame: Storage.Frame) {
        this.assertFrameCollectionPresenterAvailable(frame.visibleLine - 1) {
            it.displaySingleFrame(frame)
        }
    }

    override fun setAllGutterIconsState(state: GutterState) {
        for (item in myGutterIconRenderers) {
            item.value.setState(state)
        }
    }

    override fun setGutterIconState(visibleLine: Int, state: GutterState) {
        assertGutterIconRendererAvailable(visibleLine - 1) { it.setState(state) }
    }

    override fun dispose() {
        val makeupModel = textEditor.editor.markupModel
        val highlighters = makeupModel.allHighlighters
        for (highlighter in highlighters) {
            if (highlighter.gutterIconRenderer is GutterIconRenderer) {
                makeupModel.removeHighlighter(highlighter)
                highlighter.dispose()
            }
        }

        myGutterIconRenderers.clear()
        myFrameCollectionPresenters.forEach { it.value.dispose() }
        myFrameCollectionPresenters.clear()
    }

    private fun assertFrameCollectionPresenterAvailable(logicalLine: Int, invoker: ((FrameCollectionPresenter) -> Unit)) {
        val presenter = myFrameCollectionPresenters[logicalLine] ?: return

        invoker.invoke(presenter)
    }

    private fun assertGutterIconRendererAvailable(logicalLine: Int, invoker: ((GutterIconRenderer) -> Unit)) {
        val gutterIconRenderer = myGutterIconRenderers[logicalLine] ?: return

        invoker.invoke(gutterIconRenderer)
    }

    private fun initializeGutterIconRenderer(visibleLine: Int, invoker: (GutterIconRenderer) -> Unit) {
        val logicalLine = visibleLine - 1
        val gutterIconRenderer = myGutterIconRenderers[logicalLine]
        if (null === gutterIconRenderer) {
            val created = GutterIconRendererFactory.make(
                editor.markupModel.addLineHighlighter(logicalLine, HighlighterLayer.LAST, null),
                logicalLine,
                actionListener = MyGutterIconRendererActionListener(this)
            )

            myGutterIconRenderers[logicalLine] = created
            invoker.invoke(created)
        } else {
            invoker.invoke(gutterIconRenderer)
        }
    }

    private fun initializeFrameCollectionPresenter(visibleLine: Int, invoker: (FrameCollectionPresenter) -> Unit) {
        val logicalLine = visibleLine - 1
        val presenter = myFrameCollectionPresenters[logicalLine]
        if (null === presenter) {
            val created = FrameCollectionFactory.makePresenter(
                projectServiceProvider,
                editor,
                visibleLine
            )

            Disposer.register(textEditor, created)
            myFrameCollectionPresenters[logicalLine] = created
            invoker.invoke(created)
        } else {
            invoker.invoke(presenter)
        }
    }

    private class MyGutterIconRendererActionListener(
        private val self: EditorControllerImpl
    ) : GutterIconRenderer.ActionListener {

        override fun performGutterIconRendererAction(gutterIconRenderer: GutterIconRenderer) {
            self.assertFrameCollectionPresenterAvailable(gutterIconRenderer.logicalLine) {
                it.handleGutterAction(gutterIconRenderer)
            }
        }

    }
}