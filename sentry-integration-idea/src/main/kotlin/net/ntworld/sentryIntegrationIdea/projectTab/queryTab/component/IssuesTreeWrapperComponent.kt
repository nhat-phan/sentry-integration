package net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component

import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.ScrollPaneFactory
import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.ComponentFactory
import net.ntworld.sentryIntegrationIdea.projectTab.queryTab.QueryTabView
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class IssuesTreeWrapperComponent(
    private val projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject,
    private val query: String,
    private val name: String,
    private val dispatcher: EventDispatcher<QueryTabView.ActionListener>
) : Component {
    val tree = ComponentFactory.makeIssuesTreeComponent(projectServiceProvider, linkedProject)
    val toolbar = IssuesTreeToolbarComponent(linkedProject, query = query, name = name, dispatcher = dispatcher)

    override val component = SimpleToolWindowPanel(true, false)

    init {
        component.setContent(
            ScrollPaneFactory.createScrollPane(tree.component, true)
        )
        component.toolbar = toolbar.component
    }
}