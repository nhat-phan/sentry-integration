package net.ntworld.sentryIntegrationIdea.editor.frame

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.util.EventDispatcher
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.Icons
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class FrameViewComponent(
    private val data: Storage.Frame,
    private val dispatcher: EventDispatcher<FrameCollectionView.ActionListener>
) : Component {
    override val component = SimpleToolWindowPanel(true, false)
    private val myBackwardFrame = MyBackwardFrame(this)
    private val myForwardFrame = MyForwardFrame(this)
    private val myJumpToTopFrame = MyJumpToTopFrame(this)
    private val myCopyLinkAction = MyCopyLinkAction(this)
    private val myOpenInBrowserAction = MyOpenInBrowserAction(this)
    private val myMainLabel = MyMainLabel(this)
    private val myVariablesWrapper = JPanel(MigLayout("wrap, insets 0", "5[right]5[fill,grow]5", "5[center]5"))
    private val myToolbar by lazy {
        val panel = JPanel(MigLayout("ins 0, fill", "5[left]push[right]", "center"))

        val leftActionGroup = DefaultActionGroup()
        leftActionGroup.add(myBackwardFrame)
        leftActionGroup.add(myForwardFrame)
        leftActionGroup.addSeparator()
        leftActionGroup.add(myJumpToTopFrame)
        leftActionGroup.addSeparator()
        leftActionGroup.add(myMainLabel)
        val leftToolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar-left",
            leftActionGroup,
            true
        )

        val rightActionGroup = DefaultActionGroup()
        rightActionGroup.add(myCopyLinkAction)
        rightActionGroup.add(myOpenInBrowserAction)
        val rightToolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar-right",
            rightActionGroup,
            true
        )

        panel.add(leftToolbar.component)
        panel.add(rightToolbar.component)
        panel
    }

    init {
        component.toolbar = myToolbar
        component.setContent(myVariablesWrapper)

        if (data.variables.isEmpty()) {
            buildNoVariablePanel()
        } else {
            buildVariablesPanels()
        }
    }

    private fun buildNoVariablePanel() {
        val label = JLabel()
        label.text = "No variables data."
        myVariablesWrapper.add(label)
    }

    private fun buildVariablesPanels() {
        for (variable in data.variables) {
            val label = JLabel()
            label.text = variable.name

            val textField = JTextField()
            textField.text = variable.value
            textField.border = null
            textField.background = null
            textField.isEditable = false

            myVariablesWrapper.add(label)
            myVariablesWrapper.add(textField)
        }
    }

    private class MyMainLabel(private val self: FrameViewComponent) : AnAction(
        "#${self.data.total - self.data.index - 1} of ${self.data.total - 1}", "", null
    ) {
        override fun actionPerformed(e: AnActionEvent) {
        }

        override fun displayTextInToolbar(): Boolean = true
    }

    private class MyJumpToTopFrame(private val self: FrameViewComponent) : AnAction(
        "Jump To Error Point", "Go to the top point of stacktrace", Icons.Gutter.LastFrame
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.dispatcher.multicaster.onJumpToTopClicked(self.data)
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = self.data.index != self.data.total - 1
        }
    }

    private class MyBackwardFrame(private val self: FrameViewComponent) : AnAction(
        "Backward", "Go to the previous point of stacktrace", AllIcons.Actions.Back
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.dispatcher.multicaster.onJumpBackwardClicked(self.data)
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = null != self.data.previous
        }
    }

    private class MyForwardFrame(private val self: FrameViewComponent) : AnAction(
        "Forward", "Go to the next point of stacktrace", AllIcons.Actions.Forward
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.dispatcher.multicaster.onJumpForwardClicked(self.data)
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = null != self.data.next
        }
    }

    private class MyOpenInBrowserAction(private val self: FrameViewComponent): AnAction(
        "Open In Browser", "Open this issue in browser", Icons.ExternalLink
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.dispatcher.multicaster.onOpenInBrowserClicked(self.data)
        }
    }

    private class MyCopyLinkAction(private val self: FrameViewComponent): AnAction(
        "Copy Issue Link", "Copy this issue link", Icons.CopyLink
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.dispatcher.multicaster.onCopyIssueLinkClicked(self.data)
        }
    }
}