package net.ntworld.sentryIntegrationIdea.editor.frame

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.impl.EditorEmbeddedComponentManager
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.impl.view.FontLayoutService
import com.intellij.openapi.util.Disposer
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.EventDispatcher
import com.intellij.util.ui.JBUI
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.AbstractView
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class FrameCollectionViewImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val editor: EditorEx,
    private val visibleLine: Int
) : AbstractView<FrameCollectionView.ActionListener>(), FrameCollectionView {
    override val dispatcher = EventDispatcher.create(FrameCollectionView.ActionListener::class.java)
    private val myDisplayedFramesSet = mutableSetOf<String>()
    private val myFrameViewComponentMap = mutableMapOf<String, FrameViewComponent>()
    private val myComponentWrapper = JBUI.Panels.simplePanel()
    private val myComponentScrollPane = MyComponentScrollPane(myComponentWrapper)
    private val myWrapper = JPanel()
    private val logicalLine = visibleLine - 1

    private val myEditorWidthWatcher = EditorTextWidthWatcher()

    init {
        myComponentScrollPane.isVisible = false
        myComponentScrollPane.cursor = Cursor.getDefaultCursor()

        myWrapper.border = BorderFactory.createMatteBorder(1, 1, 1, 1, JBColor.border())
        myWrapper.layout = GridBagLayout()
        myWrapper.add(myComponentScrollPane)

        editor.scrollPane.viewport.addComponentListener(myEditorWidthWatcher)
        Disposer.register(this, Disposable {
            editor.scrollPane.viewport.removeComponentListener(myEditorWidthWatcher)
        })

        val editorEmbeddedComponentManager = EditorEmbeddedComponentManager.getInstance()
        val offset = editor.document.getLineEndOffset(logicalLine)
        editorEmbeddedComponentManager.addComponent(
            editor,
            myWrapper,
            projectServiceProvider.applicationServiceProvider.intellijIdeApi.makeEditorEmbeddedComponentManagerProperties(offset)
        )

        EditorUtil.disposeWithEditor(editor, this)
    }

    override fun isDisplayAnyFrame(): Boolean = myDisplayedFramesSet.isNotEmpty()

    override fun displaySingleFrame(frame: Storage.Frame) {
        if (!myFrameViewComponentMap.containsKey(frame.id)) {
            val frameViewComponent = FrameViewComponent(frame, dispatcher)
            myComponentWrapper.addToCenter(frameViewComponent.component)
            myFrameViewComponentMap[frame.id] = frameViewComponent
        }

        var hasFrame = false
        for (item in myFrameViewComponentMap) {
            if (item.key == frame.id) {
                item.value.component.isVisible = true
                hasFrame = true
                myDisplayedFramesSet.add(item.key)
            } else {
                item.value.component.isVisible = false
            }
        }

        if (hasFrame) {
            myComponentScrollPane.isVisible = true
            myEditorWidthWatcher.updateWidthForAllInlays()
        }
    }

    override fun hideAll() {
        myDisplayedFramesSet.clear()
        myComponentScrollPane.isVisible = false
        myEditorWidthWatcher.updateWidthForAllInlays()
    }

    override fun dispose() {
        editor.scrollPane.viewport.removeComponentListener(myEditorWidthWatcher)
    }

    private inner class MyComponentScrollPane(private val component: JComponent) : JBScrollPane(component) {
        init {
            isOpaque = false
            viewport.isOpaque = false

            border = JBUI.Borders.empty()
            viewportBorder = JBUI.Borders.empty()

            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBar.preferredSize = Dimension(0, 0)
            setViewportView(component)

            component.addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent) {
                    return dispatchEvent(ComponentEvent(component, ComponentEvent.COMPONENT_RESIZED))
                }
            })
        }

        override fun getPreferredSize(): Dimension {
            return Dimension(
                myEditorWidthWatcher.editorTextWidth,
                if (component.isVisible) component.preferredSize.height else 0
            )
        }
    }

    private inner class EditorTextWidthWatcher : ComponentAdapter() {
        var editorTextWidth: Int = 0

        private val maximumEditorTextWidth: Int
        private val verticalScrollbarFlipped: Boolean

        init {
            val metrics = (editor as EditorImpl).getFontMetrics(Font.PLAIN)
            val spaceWidth = FontLayoutService.getInstance().charWidth2D(metrics, ' '.toInt())
            maximumEditorTextWidth = ceil(spaceWidth * (editor.settings.getRightMargin(editor.project)) - 1).toInt()

            val scrollbarFlip = editor.scrollPane.getClientProperty(JBScrollPane.Flip::class.java)
            verticalScrollbarFlipped = scrollbarFlip == JBScrollPane.Flip.HORIZONTAL ||
                scrollbarFlip == JBScrollPane.Flip.BOTH
        }

        override fun componentResized(e: ComponentEvent) = updateWidthForAllInlays()
        override fun componentHidden(e: ComponentEvent) = updateWidthForAllInlays()
        override fun componentShown(e: ComponentEvent) = updateWidthForAllInlays()

        fun updateWidthForAllInlays() {
            val newWidth = calcWidth()
            editorTextWidth = newWidth

            myWrapper.dispatchEvent(ComponentEvent(myWrapper, ComponentEvent.COMPONENT_RESIZED))
            myWrapper.invalidate()
        }

        private fun calcWidth(): Int {
            val visibleEditorTextWidth =
                editor.scrollPane.viewport.width - getVerticalScrollbarWidth() - getGutterTextGap()
            return min(max(visibleEditorTextWidth, 0), maximumEditorTextWidth)
        }

        private fun getVerticalScrollbarWidth(): Int {
            val width = editor.scrollPane.verticalScrollBar.width
            return if (!verticalScrollbarFlipped) width * 2 else width
        }

        private fun getGutterTextGap(): Int {
            return if (verticalScrollbarFlipped) {
                val gutter = editor.gutterComponentEx
                gutter.width - gutter.whitespaceSeparatorOffset
            } else 0
        }
    }
}