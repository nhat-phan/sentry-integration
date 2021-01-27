package net.ntworld.sentryIntegrationIdea.node.issue

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.ErrorLevel
import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.Icons
import net.ntworld.sentryIntegrationIdea.node.AbstractNode
import net.ntworld.sentryIntegrationIdea.util.TextChoiceUtil

class IssueNode(
    val sentryIssue: SentryIssue,
    private val displayEventCount: Boolean,
    private val pluginConfiguration: PluginConfiguration
) : AbstractNode() {
    override val id: String = sentryIssue.id

    override fun updatePresentation(presentation: PresentationData) {
        if (displayEventCount && !pluginConfiguration.showEventCountAtTheEndOfIssueNode) {
            presentEventCount(presentation)
            presentation.addText(" · ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }

        presentation.addText(
            sentryIssue.title,
            if (sentryIssue.isSubscribed || !pluginConfiguration.grayOutUnsubscribeIssue) {
                if (sentryIssue.hasSeen) SimpleTextAttributes.REGULAR_ATTRIBUTES else SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES
            } else {
                SimpleTextAttributes.GRAYED_ATTRIBUTES
            }
        )

        if (displayEventCount && pluginConfiguration.showEventCountAtTheEndOfIssueNode) {
            presentation.addText(" · ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            presentEventCount(presentation)
        }

        if (sentryIssue.isBookmarked) {
            presentation.setIcon(Icons.BookmarkedTreeIcon)
            return
        }

        if (!sentryIssue.isSubscribed) {
            presentation.setIcon(Icons.Unsubscribe)
            return
        }

        if (pluginConfiguration.displayErrorLevelIcon) {
            presentErrorLevelIcon(presentation)
        }
    }

    private fun presentErrorLevelIcon(presentation: PresentationData) {
        presentation.setIcon(
            when (sentryIssue.level) {
                ErrorLevel.Info -> AllIcons.General.Information
                ErrorLevel.Warning -> AllIcons.General.Warning
                ErrorLevel.Error -> AllIcons.General.Error
            }
        )
    }

    private fun presentEventCount(presentation: PresentationData) {
        presentation.addText(
            findCountText(sentryIssue.count),
            if (sentryIssue.isSubscribed || !pluginConfiguration.grayOutUnsubscribeIssue) {
                SimpleTextAttributes.SYNTHETIC_ATTRIBUTES
            } else {
                SimpleTextAttributes.GRAYED_ATTRIBUTES
            }
        )
    }

    private fun findCountText(count: Int): String {
        if (count < 1000) {
            return TextChoiceUtil.events(count)
        }

        if (count < 1000000) {
            return (count / 1000).toString() + "k " + TextChoiceUtil.events(null)
        }

        if (count < 1000000000) {
            return (count / 1000000).toString() + "m " + TextChoiceUtil.events(null)
        }
        return (count / 1000000000).toString() + "G " + TextChoiceUtil.events(null)
    }
}