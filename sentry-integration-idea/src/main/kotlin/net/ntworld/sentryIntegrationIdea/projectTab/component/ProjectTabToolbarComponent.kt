package net.ntworld.sentryIntegrationIdea.projectTab.component

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.Messages
import com.intellij.util.EventDispatcher
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.cache.CacheManager
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.Scope
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.Icons
import net.ntworld.sentryIntegrationIdea.projectTab.ProjectTabView
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.toolWindow.MainToolWindowManager
import javax.swing.JPanel

class ProjectTabToolbarComponent(
    private val projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject,
    private val dispatcher: EventDispatcher<ProjectTabView.ActionListener>
): Component {
    private val myDeleteQueryTab = MyDeleteQueryTab(this)
    private val myMergeIssuesAction = MyMergeIssuesAction(this)
    private val myOpenProjectManagerAction = MyOpenProjectManagerAction(this)
    private val myClearCacheAction = MyClearCacheAction(this)
    private var myMultipleIssueActionsEnabled = false

    override val component by lazy {
        val panel = JPanel(MigLayout("fill", "center", ""))
        val topActionGroup = DefaultActionGroup()
        topActionGroup.add(myDeleteQueryTab)
        topActionGroup.addSeparator()
        topActionGroup.add(myMergeIssuesAction)

        val topToolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar",
            topActionGroup,
            false
        )

        val bottomActionGroup = DefaultActionGroup()
        bottomActionGroup.add(myClearCacheAction)
        bottomActionGroup.addSeparator()
        bottomActionGroup.add(myOpenProjectManagerAction)
        val bottomToolbar = ActionManager.getInstance().createActionToolbar(
            "${this::class.java.canonicalName}/toolbar",
            bottomActionGroup,
            false
        )

        panel.add(topToolbar.component, "dock north")
        panel.add(bottomToolbar.component, "dock south")
        panel
    }

    fun disableMultipleIssuesActions() {
        myMultipleIssueActionsEnabled = false
    }

    fun enableMultipleIssuesActions() {
        myMultipleIssueActionsEnabled = true
    }

    private class MyDeleteQueryTab(private val self: ProjectTabToolbarComponent) : AnAction(
        "Delete Tab", "Delete selected query tab", Icons.Trash
    ) {
        override fun actionPerformed(e: AnActionEvent) {
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = false
            e.presentation.text = "Custom Tab Feature - coming soon"
            e.presentation.description = "Custom Tab Feature - coming soon"
        }
    }

    private class MyMergeIssuesAction(private val self: ProjectTabToolbarComponent) : AnAction(
        "Merge Issues", "Merge the selected issues", AllIcons.Actions.GroupByPrefix
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.dispatcher.multicaster.onMergeIssuesClicked()
        }

        override fun update(e: AnActionEvent) {
            if (!self.linkedProject.connectionScope.canMutateIssues()) {
                e.presentation.isEnabled = false
                e.presentation.text = "Merge Issues - requires \"project:write\" scope!"
                e.presentation.description = "Merge Issues - requires \"project:write\" scope!"
                return
            } else {
                e.presentation.isEnabled = self.myMultipleIssueActionsEnabled
                e.presentation.text = "Merge Issues"
                e.presentation.description = "Merge the selected issues"
            }
        }
    }

    private class MyClearCacheAction(private val self: ProjectTabToolbarComponent) : AnAction(
        "Clear Cache", "Clear cache of the project include worker data", Icons.ClearCache
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            val result = Messages.showYesNoDialog(
                "Do you want to delete whole cache directory which is \"${self.linkedProject.cacheBasePath}\"?",
                "Are you sure",
                Messages.getQuestionIcon()
            )
            if (result == Messages.YES) {
                CacheManager.clear(self.linkedProject)
            }
        }
    }

    private class MyOpenProjectManagerAction(private val self: ProjectTabToolbarComponent) : AnAction(
        "Project Manager", "Open project manager", AllIcons.Nodes.ModuleGroup
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            self.projectServiceProvider.project.messageBus.syncPublisher(MainToolWindowManager.TOPIC).requestOpenProjectManager()
        }
    }
}