package net.ntworld.sentryIntegrationIdea.node.issue

import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.node.AbstractNode

class DetailLoadingNode(
    private val sentryIssueId: String
) : AbstractNode() {
    override val id: String = "DetailLoadingNode:${sentryIssueId}"

    override fun updatePresentation(presentation: PresentationData) {
        presentation.addText("Loading detail...", SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES)
    }
}