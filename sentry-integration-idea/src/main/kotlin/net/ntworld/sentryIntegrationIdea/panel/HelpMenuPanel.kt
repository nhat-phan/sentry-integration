package net.ntworld.sentryIntegrationIdea.panel;

import com.intellij.ide.BrowserUtil
import com.intellij.util.ui.JBUI
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.toolWindow.MainToolWindowManager
import java.awt.Cursor
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.*;

class HelpMenuPanel(private val projectServiceProvider: ProjectServiceProvider): Component {
    var myWrapper: JPanel? = null
    var myTitleLabel: JLabel? = null
    var myOpenProjectManagerLabel: JLabel? = null
    var myOpenSetupWizardLabel: JLabel? = null
    var myOpenGithubLabel: JLabel? = null


    init {
        myTitleLabel!!.foreground = JBUI.CurrentTheme.Link.linkColor()

        myOpenProjectManagerLabel!!.cursor = Cursor(Cursor.HAND_CURSOR)
        myOpenSetupWizardLabel!!.cursor = Cursor(Cursor.HAND_CURSOR)
        myOpenGithubLabel!!.cursor = Cursor(Cursor.HAND_CURSOR)

        myOpenProjectManagerLabel!!.addMouseListener(MyLabelMouseListener() {
            projectServiceProvider.project.messageBus.syncPublisher(MainToolWindowManager.TOPIC).requestOpenProjectManager()
        })
        myOpenSetupWizardLabel!!.addMouseListener(MyLabelMouseListener() {
            projectServiceProvider.project.messageBus.syncPublisher(MainToolWindowManager.TOPIC).requestOpenSetupWizard()
        })
        myOpenGithubLabel!!.addMouseListener(MyLabelMouseListener() {
            BrowserUtil.open("https://github.com/nhat-phan/sentry-integration/issues")
        })
    }

    override val component: JComponent = myWrapper!!

    private class MyLabelMouseListener(private val invoker: () -> Unit): MouseListener {
        override fun mouseClicked(e: MouseEvent?) {
            invoker.invoke()
            if (null === e || e.component !is JLabel) {
                return
            }
            e.component.foreground = JBUI.CurrentTheme.Label.foreground()
        }

        override fun mousePressed(e: MouseEvent?) {
        }

        override fun mouseReleased(e: MouseEvent?) {
        }

        override fun mouseEntered(e: MouseEvent?) {
            if (null === e || e.component !is JLabel) {
                return
            }
            e.component.foreground = JBUI.CurrentTheme.Link.linkHoverColor()
        }

        override fun mouseExited(e: MouseEvent?) {
            if (null === e || e.component !is JLabel) {
                return
            }
            e.component.foreground = JBUI.CurrentTheme.Label.foreground()
        }
    }
}
