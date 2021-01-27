package net.ntworld.sentryIntegrationIdea.node.issue

import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.node.AbstractNode

class SeenTimeNode(
    val sentryIssue: SentryIssue
) : AbstractNode() {
    override val id: String = "SeenTimeNode:${sentryIssue.id}"

    override fun updatePresentation(presentation: PresentationData) {
        presentation.addText("Last seen: ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        presentation.addText(sentryIssue.lastSeen.format(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
        presentation.addText(" (${sentryIssue.lastSeen.pretty()})", SimpleTextAttributes.REGULAR_ATTRIBUTES)

        if (sentryIssue.firstSeen != sentryIssue.lastSeen) {
            presentation.addText(" · ", SimpleTextAttributes.REGULAR_ATTRIBUTES)
            presentation.addText("First seen: ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            presentation.addText(sentryIssue.firstSeen.format(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
            presentation.addText(" (${sentryIssue.firstSeen.pretty()})", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
    }
}