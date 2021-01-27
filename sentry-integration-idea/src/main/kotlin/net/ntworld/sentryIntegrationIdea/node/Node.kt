package net.ntworld.sentryIntegrationIdea.node

import com.intellij.ide.projectView.PresentationData
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

interface Node {
    val id: String

    var parent: Node?

    val children: List<Node>

    val childCount
        get() = children.size

    fun add(node: Node)

    fun updatePresentation(presentation: PresentationData)

    open fun updatePresentation(
        projectServiceProvider: ProjectServiceProvider,
        linkedProject: LinkedProject?,
        presentation: PresentationData
    ) {
        updatePresentation(presentation)
    }
}