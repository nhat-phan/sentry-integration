package net.ntworld.sentryIntegrationIdea.node.issue

import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.node.AbstractNode

class CulpritNode(
    val sentryIssue: SentryIssue
) : AbstractNode() {
    override val id: String = "CulpritNode:${sentryIssue.id}"

    override fun updatePresentation(presentation: PresentationData) {
        if (sentryIssue.culprit.isDifferent()) {
            presentation.addText(sentryIssue.culprit.sentryRootPath, SimpleTextAttributes.GRAYED_ATTRIBUTES)
            presentation.addText(sentryIssue.culprit.value, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        } else {
            presentation.addText(sentryIssue.culprit.originValue, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
    }
}