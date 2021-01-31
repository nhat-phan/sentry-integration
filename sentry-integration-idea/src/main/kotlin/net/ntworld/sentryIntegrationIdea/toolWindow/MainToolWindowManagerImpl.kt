package net.ntworld.sentryIntegrationIdea.toolWindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ex.ToolWindowEx
import net.ntworld.sentryIntegration.debug
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.Icons
import net.ntworld.sentryIntegrationIdea.help.HelpManager
import net.ntworld.sentryIntegrationIdea.license.LicenseManager
import net.ntworld.sentryIntegrationIdea.notifier.ConnectionNotifier
import net.ntworld.sentryIntegrationIdea.notifier.LinkedProjectNotifier
import net.ntworld.sentryIntegrationIdea.projectManager.ProjectManagerFactory
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.setupWizard.SetupWizardFactory

class MainToolWindowManagerImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val toolWindow: ToolWindow
): MainToolWindowManager, Disposable {
    private val myLicenseManager = LicenseManager(projectServiceProvider, toolWindow)
    private val myHelpManager = HelpManager(projectServiceProvider, toolWindow)
    private val myProjectTabManager = ProjectTabManager(projectServiceProvider, toolWindow)
    private val myOpenSetupWizardAction = MyOpenSetupWizardAction(this)
    private val myOpenProjectManagerAction = MyOpenProjectManagerAction(this)
    private val myOpenHelpAction = MyOpenHelpAction(this)
    private val myOpenLinkedProjectAction = MyOpenLinkedProjectAction(this)
    private val myMessageBusConnection = projectServiceProvider.project.messageBus.connect()
    private val myAdditionalGearActionGroup by lazy {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(myOpenSetupWizardAction)
        actionGroup.add(myOpenProjectManagerAction)
        actionGroup.add(myOpenHelpAction)

        actionGroup
    }

    private val mySetupWizard = ToolWindowTab(
        toolWindow,
        ToolWindowTab.Properties("Setup Wizard", disposeAfterRemoved = true)
    ) {
        SetupWizardFactory.makeSetupWizard(projectServiceProvider)
    }

    private val myProjectManager = ToolWindowTab(
        toolWindow,
        ToolWindowTab.Properties("Project Manager")
    ) {
        ProjectManagerFactory.makeProjectManagerPresenter(projectServiceProvider, myProjectTabManager)
    }

    private val myConnectionNotifier = object : ConnectionNotifier {
        override fun connectionsChanged(connections: List<Connection>) {
            if (connections.isEmpty()) {
                requestOpenSetupWizard()
            }
        }
    }

    private val myLinkedProjectNotifier = object : LinkedProjectNotifier {
        override fun linkedProjectsChanged(linkedProjects: List<LinkedProject>) {
            if (linkedProjects.isEmpty()) {
                requestOpenSetupWizard()
            }
            myProjectTabManager.setLinkedProjects(linkedProjects)
        }
    }

    init {
        if (toolWindow is ToolWindowEx) {
            initialize(toolWindow)
        }
        debug("MainToolWindowManagerImpl start subscribe to Notifier")
        myMessageBusConnection.subscribe(MainToolWindowManager.TOPIC, this)
        myMessageBusConnection.subscribe(ConnectionNotifier.TOPIC, myConnectionNotifier)
        myMessageBusConnection.subscribe(LinkedProjectNotifier.TOPIC, myLinkedProjectNotifier)
        myProjectTabManager.setLinkedProjects(projectServiceProvider.linkedProjects)

        if (projectServiceProvider.connections.isEmpty() || projectServiceProvider.linkedProjects.isEmpty()) {
            requestOpenSetupWizard()
        }

        Disposer.register(toolWindow.disposable, this)
    }

    private fun initialize(toolWindowEx: ToolWindowEx) {
        toolWindowEx.setAdditionalGearActions(myAdditionalGearActionGroup)
        toolWindowEx.setTabActions(myOpenLinkedProjectAction)
    }

    override fun requestOpenProjectManager() = ApplicationManager.getApplication().invokeLater { myProjectManager.open() }
    override fun requestOpenSetupWizard() = ApplicationManager.getApplication().invokeLater { mySetupWizard.open() }
    override fun requestCloseSetupWizard() = ApplicationManager.getApplication().invokeLater { mySetupWizard.close() }

    override fun dispose() {
        myMessageBusConnection.disconnect()
        myLicenseManager.dispose()
    }

    private class MyOpenLinkedProjectAction(private val self: MainToolWindowManagerImpl) : AnAction(
        "Open Project", "Open project", Icons.OpenProject
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val dialog = OpenProjectsDialog(self.projectServiceProvider, self.projectServiceProvider.linkedProjects)
            val data = dialog.openDialog()
            self.myProjectTabManager.openLinkedProjects(data)
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isVisible = self.myProjectTabManager.hasAnyProjectReadyToOpen()
        }
    }

    private class MyOpenSetupWizardAction(private val self: MainToolWindowManagerImpl) : AnAction(
        "Open Setup Wizard", "Open setup wizard", Icons.SetupWizard
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.requestOpenSetupWizard()
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = !self.mySetupWizard.isOpened
        }
    }

    private class MyOpenProjectManagerAction(private val self: MainToolWindowManagerImpl) : AnAction(
        "Open Project Manager", "Open project manager", AllIcons.Nodes.ModuleGroup
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.requestOpenProjectManager()
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = !self.myProjectManager.isOpened
        }
    }

    private class MyOpenHelpAction(private val self: MainToolWindowManagerImpl): AnAction(
        "Open Help Center", "Open help center", AllIcons.Actions.Help
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.myHelpManager.open()
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = !self.myHelpManager.isOpened
        }
    }
}