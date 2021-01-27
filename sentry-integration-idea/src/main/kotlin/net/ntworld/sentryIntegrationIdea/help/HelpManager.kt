package net.ntworld.sentryIntegrationIdea.help

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.panel.HelpMenuPanel
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.toolWindow.ToolWindowTab
import javax.swing.JPanel

class HelpManager(
    private val projectServiceProvider: ProjectServiceProvider,
    private val toolWindow: ToolWindow
) {
    private val tab = ToolWindowTab(toolWindow, ToolWindowTab.Properties(tabName = "Help", disposeAfterRemoved = true)) {
        HelpComponent(projectServiceProvider)
    }

    private val myContentManagerListener = object: ContentManagerListener {
        override fun contentRemoved(event: ContentManagerEvent) {
            if (toolWindow.contentManager.contents.count() == 0) {
                ApplicationManager.getApplication().invokeLater {
                    tab.open()
                }
            }
        }
    }

    val isOpened
        get() = tab.isOpened

    init {
        toolWindow.addContentManagerListener(myContentManagerListener)
    }

    fun open() = tab.open()

    private class HelpComponent(private val projectServiceProvider: ProjectServiceProvider): Component {
        override val component: OnePixelSplitter = OnePixelSplitter(false, 0.4f)
        private val myHelpMenuPanel = HelpMenuPanel(projectServiceProvider)

        init {
            val dummy = JPanel()
            component.firstComponent = myHelpMenuPanel.component
            component.secondComponent = dummy
        }
    }
}