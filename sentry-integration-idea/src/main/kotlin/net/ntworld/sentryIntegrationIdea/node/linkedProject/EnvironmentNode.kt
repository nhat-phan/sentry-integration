package net.ntworld.sentryIntegrationIdea.node.linkedProject

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.node.AbstractNode

class EnvironmentNode(val linkedProject: LinkedProject): AbstractNode() {
    override val id: String = linkedProject.id

    override fun updatePresentation(presentation: PresentationData) {
        presentation.addText(linkedProject.environmentName, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        when (linkedProject.state) {
            LinkedProject.State.INITIALIZE -> {
                presentation.setIcon(AllIcons.Actions.Refresh)
            }
            LinkedProject.State.READY -> {
                presentation.setIcon(AllIcons.Nodes.Related)
            }
            else -> {
                presentation.setIcon(AllIcons.General.Error)
            }
        }
    }
}