package net.ntworld.sentryIntegrationIdea.setupWizard.component

import com.intellij.ui.JBColor
import com.intellij.ui.ScrollPaneFactory
import com.intellij.util.EventDispatcher
import com.intellij.util.ui.JBUI
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegrationIdea.Icons
import net.ntworld.sentryIntegrationIdea.setupWizard.SetupWizardView
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

class SelectConnectionComponent(
    private val dispatcher: EventDispatcher<SetupWizardView.ActionListener>,
    private val connections: List<Connection>,
    private val selected: String?
): AbstractWizardStep("STEP 1/3: SELECT CONNECTION") {
    private val myItemConnectionMap = mutableMapOf<JPanel, Connection>()
    private val myCreateNewConnectionButton = JButton()
    private val myItemMouseListener = object : MouseListener {
        private fun assertEventFromItemPanel(e: MouseEvent?, invoker: (JPanel, Connection) -> Unit) {
            if (!isActive) {
                return
            }

            if (null === e) {
                return
            }

            val panel = e.component
            if (panel !is JPanel) {
                return
            }

            val connection = myItemConnectionMap[panel]
            if (null === connection) {
                return
            }
            invoker.invoke(panel, connection)
        }

        override fun mouseReleased(e: MouseEvent?) {
        }

        override fun mouseEntered(e: MouseEvent?) = assertEventFromItemPanel(e) { panel, _ ->
            panel.border = BorderFactory.createMatteBorder(3, 3, 3, 3, JBUI.CurrentTheme.Focus.focusColor())
        }

        override fun mouseClicked(e: MouseEvent?) = assertEventFromItemPanel(e) { panel, connection ->
            panel.border = BorderFactory.createMatteBorder(3, 3, 3, 3, JBUI.CurrentTheme.Link.linkColor())
            dispatcher.multicaster.onConnectionSelected(connection)
        }

        override fun mouseExited(e: MouseEvent?) = assertEventFromItemPanel(e) { panel, connection ->
            if (null !== selected && selected == connection.id) {
                panel.border = BorderFactory.createMatteBorder(3, 3, 3, 3, JBUI.CurrentTheme.Link.linkColor())
            } else {
                panel.border = BorderFactory.createMatteBorder(3, 3, 3, 3, JBColor.border())
            }
        }

        override fun mousePressed(e: MouseEvent?) {
        }
    }

    init {
        val myWrapper = JPanel(MigLayout("wrap, gap 10", "[fill,grow]"))

        for (connection in connections) {
            val item = JPanel(MigLayout("wrap", "[right]5[fill,grow]", "10[]10"))

            item.add(makeLabel("Server Url:"))
            item.add(makeLabel(connection.url))

            item.add(makeLabel("Auth Token:"))
            item.add(makeLabel(connection.getCensoredToken()))

            val user = connection.user
            item.add(makeLabel("User:"))
            if (null !== user) {
                item.add(makeLabel(user.name + " (" + user.username + ")"))
            } else {
                item.add(makeLabel("Unknown"))
            }

            item.add(makeLabel("Status:"))
            when (connection.getStatus()) {
                Connection.Status.WARNING -> {
                    val label = makeLabel("Missing scope \"project:write\"")
                    label.foreground = JBColor.yellow
                    item.add(label)
                }
                Connection.Status.ALL_GOOD -> {
                    val label = makeLabel("All good")
                    label.icon = Icons.Resolved
                    item.add(label)
                }
            }

            if (null !== selected && selected == connection.id) {
                item.border = BorderFactory.createMatteBorder(3, 3, 3, 3, JBUI.CurrentTheme.Link.linkColor())
            } else {
                item.border = BorderFactory.createMatteBorder(3, 3, 3, 3, JBColor.border())
            }

            myItemConnectionMap[item] = connection
            item.addMouseListener(myItemMouseListener)
            myWrapper.add(item, "wrap")
        }

        val myButtonWrapper = JPanel(MigLayout("wrap, insets 0", "10[]push", "10[center]10"))
        myCreateNewConnectionButton.text = "Create New Connection"
        myButtonWrapper.add(myCreateNewConnectionButton)
        myCreateNewConnectionButton.addActionListener {
            dispatcher.multicaster.onCreateConnectionButtonClicked()
        }
        component.add(myButtonWrapper, "dock south")

        component.add(ScrollPaneFactory.createScrollPane(myWrapper, true), "wrap")
    }

    override fun onStateChanged() {
        myCreateNewConnectionButton.isEnabled = isActive
    }
}