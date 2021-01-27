package net.ntworld.sentryIntegrationIdea.node.issue

import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.node.AbstractNode
import net.ntworld.sentryIntegrationIdea.util.EventTagsUtil

class TagsNode(
    val sentryIssue: SentryIssue,
    val tags: List<EventTagsUtil.TagData>
): AbstractNode() {
    override val id: String = "TagsNode:${sentryIssue.id}"

    override fun updatePresentation(presentation: PresentationData) {
        for (i in 0..tags.lastIndex) {
            presentation.addText(tags[i].key + ": ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            presentation.addText(tags[i].value, SimpleTextAttributes.REGULAR_ATTRIBUTES)
            if (i != tags.lastIndex) {
                presentation.addText(" · ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            }
        }
    }
}