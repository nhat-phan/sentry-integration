package net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.util.EventDispatcher
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.Icons
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.QueryTabView
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel

class SingleIssueToolbarComponent(
    private val linkedProject: LinkedProject,
    private val dispatcher: EventDispatcher<QueryTabView.ActionListener>
) : Component {
    private val myOpenInBrowserLink = MyOpenInBrowserAction(this)
    private val myCopyLinkAction = MyCopyLinkAction(this)
    private val myBookmarkAction = MyBookmarkAction(this)
    private val myRemoveBookmarkAction = MyRemoveBookmarkAction(this)
    private val myMarkAsSeenAction = MyMarkAsSeenAction(this)
    private val myMarkAsUnseenAction = MyMarkAsUnseenAction(this)
    private val mySubscribeAction = MySubscribeAction(this)
    private val myUnsubscribeAction = MyUnsubscribeAction(this)
    private val myResolveAction = MyResolveAction(this)
    private val myUnresolveAction = MyUnresolveAction(this)
    // private val myIgnoreAction = MyIgnoreAction(this)
    private var myIssue: SentryIssue? = null

    override val component : JComponent by lazy {
        val panel = JPanel(MigLayout("fill", "center", ""))
        val topActionGroup = DefaultActionGroup()
        topActionGroup.add(myOpenInBrowserLink)
        topActionGroup.add(myCopyLinkAction)
        topActionGroup.addSeparator()
        topActionGroup.add(myMarkAsSeenAction)
        topActionGroup.add(myMarkAsUnseenAction)
        topActionGroup.add(mySubscribeAction)
        topActionGroup.add(myUnsubscribeAction)
        topActionGroup.add(myBookmarkAction)
        topActionGroup.add(myRemoveBookmarkAction)
        topActionGroup.addSeparator()
        topActionGroup.add(myResolveAction)
        topActionGroup.add(myUnresolveAction)
        // topActionGroup.add(myIgnoreAction)

        val topToolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar-top",
            topActionGroup,
            false
        )

        panel.add(topToolbar.component, "dock north")
        panel
    }

    fun setIssue(sentryIssue: SentryIssue) {
        myIssue = sentryIssue
    }

    private class MyOpenInBrowserAction(private val self: SingleIssueToolbarComponent): AnAction(
        "Open In Browser", "Open this issue in browser", Icons.ExternalLink
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.OPEN_IN_BROWSER, null)
            }
        }
    }

    private class MyCopyLinkAction(private val self: SingleIssueToolbarComponent): AnAction(
        "Copy Issue Link", "Copy this issue link", Icons.CopyLink
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.COPY_LINK, null)
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    private abstract class AbstractMutableAction(
        protected val self: SingleIssueToolbarComponent,
        private val text: String,
        private val description: String,
        private val missingScopeDesc: String,
        icon: Icon
    ): AnAction(text, description, icon) {
        abstract fun isVisible(issue: SentryIssue): Boolean

        override fun update(e: AnActionEvent) {
            val issue = self.myIssue
            e.presentation.isVisible = null !== issue && isVisible(issue)

            if (!self.linkedProject.connectionScope.canMutateIssues()) {
                e.presentation.isEnabled = false
                e.presentation.text = missingScopeDesc
                e.presentation.description = missingScopeDesc
            } else {
                e.presentation.isEnabled = true
                e.presentation.text = text
                e.presentation.description = description
            }
        }
    }

    private class MyMarkAsSeenAction(self: SingleIssueToolbarComponent): AbstractMutableAction(
        self,
        "Mark As Seen", "Mark this issue as seen",
        "Mark As Seen - requires \"project:write\" scope!",
        Icons.Seen
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.MARK_AS_SEEN, null)
            }
        }

        override fun isVisible(issue: SentryIssue): Boolean = !issue.hasSeen
    }

    private class MyMarkAsUnseenAction(self: SingleIssueToolbarComponent): AbstractMutableAction(
        self,
        "Mark As Unseen", "Mark this issue as unseen",
        "Mark As Unseen - requires \"project:write\" scope!",
        Icons.Unseen
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.MARK_AS_UNSEEN, null)
            }
        }

        override fun isVisible(issue: SentryIssue): Boolean = issue.hasSeen
    }

    private class MySubscribeAction(self: SingleIssueToolbarComponent): AbstractMutableAction(
        self,
        "Subscribe", "Subscribe this issue",
        "Subscribe - requires \"project:write\" scope!",
        Icons.Subscribe
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.SUBSCRIBE, null)
            }
        }

        override fun isVisible(issue: SentryIssue): Boolean = !issue.isSubscribed
    }

    private class MyUnsubscribeAction(self: SingleIssueToolbarComponent): AbstractMutableAction(
        self,
        "Unsubscribe", "Unsubscribe this issue",
        "Unsubscribe - requires \"project:write\" scope!",
        Icons.Unsubscribe
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.UNSUBSCRIBE, null)
            }
        }

        override fun isVisible(issue: SentryIssue): Boolean = issue.isSubscribed
    }

    private class MyBookmarkAction(self: SingleIssueToolbarComponent): AbstractMutableAction(
        self,
        "Bookmark", "Bookmark this issue",
        "Bookmark Issue - requires \"project:write\" scope!",
        Icons.Bookmark
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.BOOKMARK, null)
            }
        }

        override fun isVisible(issue: SentryIssue): Boolean = !issue.isBookmarked
    }

    private class MyRemoveBookmarkAction(self: SingleIssueToolbarComponent): AbstractMutableAction(
        self,
        "Remove Bookmark", "Remove bookmark of this issue",
        "Remove Bookmark - requires \"project:write\" scope!",
        Icons.Bookmarked
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.REMOVE_BOOKMARK, null)
            }
        }

        override fun isVisible(issue: SentryIssue): Boolean = issue.isBookmarked
    }

    private class MyResolveAction(self: SingleIssueToolbarComponent): AbstractMutableAction(
        self,
        "Resolve", "Change status of this issue to resolved",
        "Resolve Issue - requires \"project:write\" scope!",
        Icons.Resolve
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.RESOLVE, null)
            }
        }

        override fun isVisible(issue: SentryIssue): Boolean = issue.status == "unresolved"
    }

    private class MyUnresolveAction(self: SingleIssueToolbarComponent): AbstractMutableAction(
        self,
        "Unresolve", "Change status of this issue to unresolved",
        "Unresolve Issue - requires \"project:write\" scope!",
        Icons.Resolved
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val issue = self.myIssue
            if (null !== issue) {
                self.dispatcher.multicaster.onSingleIssueActionClicked(issue, QueryTabView.SingleIssueAction.UNRESOLVE, null)
            }
        }

        override fun isVisible(issue: SentryIssue): Boolean = issue.status == "resolved"
    }

    private class MyIgnoreAction(private val self: SingleIssueToolbarComponent) : AnAction(
        "Ignore", "Ignore this issue", Icons.Ignore
    ) {
        override fun actionPerformed(e: AnActionEvent) {
        }
    }
}