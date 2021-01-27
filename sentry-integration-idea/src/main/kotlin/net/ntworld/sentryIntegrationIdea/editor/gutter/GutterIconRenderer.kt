package net.ntworld.sentryIntegrationIdea.editor.gutter

interface GutterIconRenderer {
    val logicalLine: Int

    fun setState(state: GutterState)

    interface ActionListener {
        fun performGutterIconRendererAction(gutterIconRenderer: GutterIconRenderer)
    }
}