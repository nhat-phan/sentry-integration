package net.ntworld.sentryIntegrationIdea.component

import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.Component
import java.util.*

interface ProjectsTreeComponent: Component {
    fun addActionListener(listener: Listener)

    fun removeActionListener(listener: Listener)

    fun setLinkedProjects(projects: List<LinkedProject>)

    fun expandAll()

    interface Listener: EventListener {
        fun onProjectTreeUnselected()

        fun onProjectSelected(projects: List<LinkedProject>, isSelectConnection: Boolean)

        fun onEnvironmentSelected(project: LinkedProject)
    }
}