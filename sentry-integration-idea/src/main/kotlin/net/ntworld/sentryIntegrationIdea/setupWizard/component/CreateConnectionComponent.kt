package net.ntworld.sentryIntegrationIdea.setupWizard.component

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.Messages
import com.intellij.util.EventDispatcher
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.cache.CacheManager
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegrationIdea.setupWizard.SetupWizardView
import net.ntworld.sentryIntegrationIdea.util.UrlUtil
import java.awt.Cursor
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class CreateConnectionComponent(
    private val dispatcher: EventDispatcher<SetupWizardView.ActionListener>,
    private val showCancelButton: Boolean
): AbstractWizardStep("STEP 1/3: CREATE CONNECTION") {
    private val myServerUrlTextField = JTextField()
    private val myTokenTextField = JTextField()
    private val myTestButton = JButton()
    private val myCancelButton = JButton()
    private val myOpenSentryUrl = JLabel()

    init {
        val myWrapper = JPanel(MigLayout("wrap, insets 0", "[right]5[fill,grow]", "10[center]"))
        myWrapper.background = UIUtil.getEditorPaneBackground()

        myWrapper.add(makeLabel("Server Url"))
        myServerUrlTextField.text = "https://sentry.io"
        myWrapper.add(myServerUrlTextField)

        myWrapper.add(makeLabel("Auth Token"))
        myWrapper.add(myTokenTextField)

        myWrapper.add(makeEmptyComponent())
        myOpenSentryUrl.foreground = JBUI.CurrentTheme.Link.linkColor()
        myOpenSentryUrl.cursor = Cursor(Cursor.HAND_CURSOR)
        myOpenSentryUrl.text = "Find or create your Authentication Token?"
        myWrapper.add(myOpenSentryUrl)

        myWrapper.add(makeEmptyComponent())
        myWrapper.add(makeLabel("<html>Please grant \"project:write\" permission along with default ones.<br /><br />Plugin works best with these scopes: \"event:admin, event:read, member:read, org:read, project:read, project:releases, team:read, project:write\"</html>"))

        component.add(myWrapper, "wrap")

        val myButtonWrapper = JPanel(MigLayout("wrap, insets 0", "10push[]10[]10", "10[center]10"))
        myButtonWrapper.background = UIUtil.getEditorPaneBackground()
        if (showCancelButton) {
            myCancelButton.text = "Cancel"
            myCancelButton.background = UIUtil.getEditorPaneBackground()
            myButtonWrapper.add(myCancelButton)
            myCancelButton.addActionListener {
                dispatcher.multicaster.onCreateConnectionCancelClicked()
            }
        }

        myTestButton.text = "Test Connection"
        myTestButton.background = UIUtil.getEditorPaneBackground()
        myButtonWrapper.add(myTestButton)
        myTestButton.addActionListener { this.onTestButtonClicked() }
        component.add(myButtonWrapper, "dock south")

        myOpenSentryUrl.addMouseListener(object : MouseListener {
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
            override fun mousePressed(e: MouseEvent?) {}

            override fun mouseClicked(e: MouseEvent?) {
                val url = UrlUtil.getBaseUrl(myServerUrlTextField.text)
                if (url.isEmpty()) {
                    BrowserUtil.open("https://sentry.io/settings/account/api/auth-tokens/")
                } else {
                    BrowserUtil.open("$url/settings/account/api/auth-tokens/")
                }
            }
        })
    }

    private fun onTestButtonClicked() {
        val url = myServerUrlTextField.text
        val token = myTokenTextField.text
        if (url.isNotEmpty() && token.isNotEmpty()) {
            try {
                val baseUrl = UrlUtil.getBaseUrl(url)
                val pair = SentryApiManager.make(baseUrl, token).getCurrentUser()
                val connection = Connection(id = "$baseUrl:${pair.first.id}", url = baseUrl, token = token, user = pair.first, scope = pair.second)
                if (connection.getStatus() == Connection.Status.WARNING) {
                    val result = Messages.showYesNoDialog(
                        "The scope \"project:write\" is not granted, you cannot use some features such as: merge issues, assign, bookmark, resolve issue.",
                        "Do you want to continue?",
                        Messages.getQuestionIcon()
                    )
                    if (result == Messages.NO) {
                        return
                    }
                }
                dispatcher.multicaster.onConnectionTested(connection)
            } catch (exception: Exception) {
                Messages.showErrorDialog(
                    "Sentry Integration plugin cannot connect with current Url and Auth Token, please check your data.",
                    "Invalid Connection"
                )
            }
        }
    }

    override fun onStateChanged() {
    }
}