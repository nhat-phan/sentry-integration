package net.ntworld.sentryIntegrationIdea.editor.gutter

import com.intellij.openapi.editor.markup.RangeHighlighter

object GutterIconRendererFactory {

    fun make(highlighter: RangeHighlighter, logicalLine: Int, actionListener: GutterIconRenderer.ActionListener): GutterIconRenderer {
        val gutterIconRenderer = GutterIconRendererImpl(logicalLine, actionListener)
        highlighter.gutterIconRenderer = gutterIconRenderer
        return gutterIconRenderer
    }
}