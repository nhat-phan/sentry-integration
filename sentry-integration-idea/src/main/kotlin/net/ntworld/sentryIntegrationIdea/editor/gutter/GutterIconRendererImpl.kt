package net.ntworld.sentryIntegrationIdea.editor.gutter

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import net.ntworld.sentryIntegrationIdea.Icons
import com.intellij.openapi.editor.markup.GutterIconRenderer as IdeaGutterIconRenderer

class GutterIconRendererImpl(
    override val logicalLine: Int,
    private val actionListener: GutterIconRenderer.ActionListener
) : GutterIconRenderer, IdeaGutterIconRenderer() {
    private var icon = Icons.Gutter.Frame
    private val clickAction = MyClickAction(this)

    override fun setState(state: GutterState) {
        when (state) {
            GutterState.LAST_FRAME -> icon = Icons.Gutter.LastFrame
            GutterState.FRAME -> icon = Icons.Gutter.Frame
        }
    }

    override fun getClickAction(): AnAction = clickAction
    override fun getIcon() = icon
    override fun getTooltipText(): String = "Sentry exception found on this line"
    override fun isNavigateAction() = true
    override fun hashCode(): Int = System.identityHashCode(this)
    override fun equals(other: Any?): Boolean = other == this

    private class MyClickAction(private val self: GutterIconRendererImpl) : AnAction() {
        override fun actionPerformed(e: AnActionEvent) {
            self.actionListener.performGutterIconRendererAction(self)
        }
    }
}