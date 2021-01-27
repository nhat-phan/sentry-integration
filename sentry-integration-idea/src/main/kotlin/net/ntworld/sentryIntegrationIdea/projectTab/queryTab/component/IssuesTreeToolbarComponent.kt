package net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.panels.Wrapper
import com.intellij.util.EventDispatcher
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryIssueSortedByEnum
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.Icons
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.QueryTabView
import java.awt.event.ItemListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JComponent
import javax.swing.JPanel

class IssuesTreeToolbarComponent(
    private val linkedProject: LinkedProject,
    private val query: String,
    private val name: String,
    private val dispatcher: EventDispatcher<QueryTabView.ActionListener>
): Component {
    private var myShowEventCount = true
    private val myQueryTextField = MyQueryTextField(this)
    private val mySortedByComboBox = ComboBox<String>(155)
    private val myRefreshAction = MyRefreshAction(this)
    private val myShowEventCountAction = MyShowEventCountAction(this)
    private val mySortedByTextMap = mapOf(
        "Sort by: Priority" to SentryIssueSortedByEnum.Priority,
        "Sort by: Last Seen" to SentryIssueSortedByEnum.LastSeen,
        "Sort by: First Seen" to SentryIssueSortedByEnum.FirstSeen,
        "Sort by: Events" to SentryIssueSortedByEnum.Events,
        "Sort by: Users" to SentryIssueSortedByEnum.Users
    )
    private val defaultSelectItem = "Sort by: Last Seen"
    private val myQueryTextFieldKeyboardListener = object : KeyListener {
        override fun keyTyped(e: KeyEvent?) {
        }

        override fun keyPressed(e: KeyEvent?) {
        }

        override fun keyReleased(e: KeyEvent?) {
            if (null === e || (e.keyCode != 10 && e.keyCode != 13)) {
                return
            }

            dispatcher.multicaster.onQueryChanged(myQueryTextField.text, refreshClicked = false)
            myQueryTextField.addCurrentTextToHistory()
        }
    }
    private val mySortedByComboBoxActionListener = ItemListener {
        val enum = mySortedByTextMap[mySortedByComboBox.selectedItem]
        if (null !== enum) {
            dispatcher.multicaster.onSortedByChanged(enum)
        }
    }

    override val component: JComponent by lazy {
        val panel = JPanel(MigLayout("ins 0, fill", "5[left]5[fill,grow][right]", "center"))

        val rightActionGroup = DefaultActionGroup()
        rightActionGroup.add(myShowEventCountAction)
        rightActionGroup.addSeparator()
        rightActionGroup.add(myRefreshAction)
        val rightToolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar-right",
            rightActionGroup,
            true
        )

        myQueryTextField.text = query
        val myQueryTextFieldWrapper = Wrapper(myQueryTextField)
        myQueryTextFieldWrapper.setVerticalSizeReferent(rightToolbar.component)

        val mySortingListWrapper = Wrapper(mySortedByComboBox)
        mySortingListWrapper.setVerticalSizeReferent(rightToolbar.component)

        panel.add(mySortedByComboBox)
        panel.add(myQueryTextFieldWrapper)
        panel.add(rightToolbar.component)
        panel
    }

    init {
        myQueryTextField.addKeyboardListener(myQueryTextFieldKeyboardListener)

        for (item in mySortedByTextMap) {
            mySortedByComboBox.addItem(item.key)
        }
        mySortedByComboBox.selectedItem = defaultSelectItem
        mySortedByComboBox.addItemListener(mySortedByComboBoxActionListener)
    }

    private class MyShowEventCountAction(private val self: IssuesTreeToolbarComponent) : ToggleAction(
        "Show Event Count", "Show events count in the tree", Icons.EventNumberInTree
    ) {
        override fun isSelected(e: AnActionEvent): Boolean {
            return self.myShowEventCount
        }

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            self.myShowEventCount = state
            self.dispatcher.multicaster.onDisplayEventCountChanged(state)
        }
    }

    private class MyRefreshAction(private val self: IssuesTreeToolbarComponent) : AnAction(
        "Refresh", "Refresh the issues", AllIcons.Actions.Refresh
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.dispatcher.multicaster.onQueryChanged(query = self.myQueryTextField.text, refreshClicked = true)
        }
    }

    private class MyQueryTextField(private val self: IssuesTreeToolbarComponent): SearchTextField() {
        init {
            setHistoryPropertyName(
                IssuesTreeToolbarComponent::class.java.canonicalName + "/${self.linkedProject.id}@${self.name}"
            )
        }

        override fun onFieldCleared() {
            self.dispatcher.multicaster.onQueryChanged(query = self.myQueryTextField.text, refreshClicked = false)
        }
    }
}