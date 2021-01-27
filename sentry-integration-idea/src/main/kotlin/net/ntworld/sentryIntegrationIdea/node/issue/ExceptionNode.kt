package net.ntworld.sentryIntegrationIdea.node.issue

import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.SentryEventException
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.node.AbstractNode

class ExceptionNode(
    val sentryIssue: SentryIssue,
    val exception: SentryEventException,
    val index: Int
): AbstractNode() {
    override val id: String = "ExceptionNode:${sentryIssue.id}:$index"

    override fun updatePresentation(presentation: PresentationData) {
        presentation.addText("Exception #${(index+1)}: ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        presentation.addText(exception.type + " · " + exception.value, SimpleTextAttributes.REGULAR_ATTRIBUTES)
    }
}