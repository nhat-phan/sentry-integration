package net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.panels.Wrapper
import com.intellij.util.EventDispatcher
import com.intellij.util.ui.UIUtil
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegration.entity.SentryEventDetail
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueAssignee
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.CustomSimpleToolWindowPanel
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.QueryTabView
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JLabel
import javax.swing.JPanel

class IssueDetailComponent(
    private val pluginConfiguration: PluginConfiguration,
    private val dispatcher: EventDispatcher<QueryTabView.ActionListener>
) : Component {
    override val component = CustomSimpleToolWindowPanel(vertical = true)
    private var isIssueDetailLoading = false
    private var isEventDetailLoading = false
    private var myIssue: SentryIssue? = null
    private val myTeams = mutableListOf<SentryTeam>()
    private val myUsers = mutableListOf<SentryUser>()
    private val myContentSimpleToolWindowPanel = SimpleToolWindowPanel(true, false)
    private val myWrapper = JPanel(MigLayout("wrap, insets 0", "5[left]5", "5[]5"))
    private val myEventDetailTagsComponent = EventDetailTagsComponent(pluginConfiguration)
    private val myIssueDetailStatsComponent = IssueDetailStatsComponent()
    private val myEventsAction = MyEventsAction(this)
    private val myTabs by lazy {
        val panel = JPanel(MigLayout("ins 0, fill", "5[fill,grow]", "center"))

        val actionGroup = DefaultActionGroup()
        actionGroup.add(myEventsAction)
        val toolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar",
            actionGroup,
            true
        )

        panel.add(toolbar.component)
        panel
    }
    private val myAssigneeMap = mutableMapOf<String, Assignee>()
    private val myAssigneeComboBox = ComboBox<Assignee>()
    private val myAssignedToLabel = JLabel()
    private val myAssigneeToolbar by lazy {
        val panel = JPanel(MigLayout("ins 0, fill", "5[left]5[fill,grow]5", "center"))

        val rightActionGroup = DefaultActionGroup()
        val rightToolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar-right",
            rightActionGroup,
            true
        )

        myAssignedToLabel.text = "Assigned To"

        val myAssigneeComboBoxWrapper = Wrapper(myAssigneeComboBox)
        myAssigneeComboBoxWrapper.setVerticalSizeReferent(rightToolbar.component)

        panel.add(myAssignedToLabel)
        panel.add(myAssigneeComboBoxWrapper)
        panel
    }
    private val myAssignedToActionListener = object : ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            val selected = myAssigneeComboBox.selectedItem
            val issue = myIssue
            if (null !== issue && null !== selected && selected is Assignee) {
                val current = if (null !== issue.assignedTo) issue.assignedTo!!.type + ":" + issue.assignedTo!!.id else ""
                val next = if (null !== selected.assignee) selected.assignee.type + ":" + selected.assignee.id else ""
                if (next != current) {
                    dispatcher.multicaster.onSingleIssueActionClicked(
                        issue,
                        QueryTabView.SingleIssueAction.CHANGE_ASSIGNEE,
                        Pair(selected.id, selected.assignee)
                    )
                }
            }
        }
    }

    init {
        myWrapper.background = UIUtil.getEditorPaneBackground()
        myWrapper.add(myIssueDetailStatsComponent.component, "newline")
        myWrapper.add(myEventDetailTagsComponent.component, "newline")

        myContentSimpleToolWindowPanel.setContent(ScrollPaneFactory.createScrollPane(myWrapper, true))
        myContentSimpleToolWindowPanel.toolbar = myAssigneeToolbar

        component.setContent(myContentSimpleToolWindowPanel)
        component.toolbar = myTabs

        myAssigneeComboBox.addActionListener(myAssignedToActionListener)
    }

    fun displayLoading() {
        isIssueDetailLoading = true
        isEventDetailLoading = true

        myAssignedToLabel.text = "Loading..."
        myAssigneeComboBox.isVisible = false
        myIssueDetailStatsComponent.showLoadingState()
        myEventDetailTagsComponent.showLoadingState()
    }

    fun setIssueDetail(issue: SentryIssue, detail: SentryIssueDetail) {
        myIssue = issue

        setSelectedAssignee()
        myAssignedToLabel.text = "Assigned To"
        myAssigneeComboBox.isVisible = true
        myIssueDetailStatsComponent.setStats(detail.twentyFourHoursStat, detail.thirtyDaysStat)

        isIssueDetailLoading = false
    }

    fun setEventDetail(issue: SentryIssue, detail: SentryEventDetail) {
        myEventDetailTagsComponent.setTags(detail.tags)

        isEventDetailLoading = false
    }

    fun setTeamsAndUsers(teams: List<SentryTeam>, users: List<SentryUser>) {
        myTeams.clear()
        myTeams.addAll(teams)

        myUsers.clear()
        myUsers.addAll(users)

        myAssigneeMap.clear()
        myAssigneeMap[""] = Assignee(
            id = "",
            text = "--- No Assignee ---",
            assignee = null
        )

        for (item in myTeams) {
            myAssigneeMap["team:" + item.id] = Assignee(
                id = "team:${item.id}",
                text = "Team: ${item.name}",
                assignee = SentryIssueAssignee(
                    id = item.id,
                    name = item.name,
                    type = "team"
                )
            )
        }

        for (item in myUsers) {
            myAssigneeMap["user:" + item.user.id] = Assignee(
                id = "user:${item.user.id}",
                text = "People: ${item.user.name}",
                assignee = SentryIssueAssignee(
                    id = item.user.id,
                    name = item.user.name,
                    type = "user"
                )
            )
        }

        myAssigneeComboBox.removeAllItems()
        for (item in myAssigneeMap) {
            myAssigneeComboBox.addItem(item.value)
        }

        setSelectedAssignee()
    }

    private fun setSelectedAssignee() {
        val issue = myIssue
        if (null !== issue) {
            val assignedTo = issue.assignedTo
            val myAssigneeComboBoxValue = if (null === assignedTo) "" else assignedTo.type + ":" + assignedTo.id
            val requested = myAssigneeMap[myAssigneeComboBoxValue]
            val current = myAssigneeComboBox.selectedItem
            if (current != requested) {
                myAssigneeComboBox.selectedItem = requested
            }
        }
    }

    private class MyEventsAction(private val self: IssueDetailComponent) : ToggleAction(
        "Events", "Show issue event information", null
    ) {
        override fun isSelected(e: AnActionEvent): Boolean {
            return true
        }

        override fun setSelected(e: AnActionEvent, state: Boolean) {
        }

        override fun displayTextInToolbar(): Boolean = true
    }

    private class Assignee(val id: String, val text: String, val assignee: SentryIssueAssignee?) {
        override fun toString(): String {
            return text
        }
    }
}