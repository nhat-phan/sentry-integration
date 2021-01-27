package net.ntworld.sentryIntegrationIdea.node.linkedProject

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.node.AbstractNode

class ConnectionNode(
    override val id: String,
    val name: String,
    val linkedProjects: List<LinkedProject>,
    val isValid: Boolean
) : AbstractNode() {

    override fun updatePresentation(presentation: PresentationData) {
        presentation.addText(name, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)

        if (isValid) {
            presentation.setIcon(AllIcons.Nodes.SecurityRole)
        } else {
            presentation.setIcon(AllIcons.General.Error)
        }
    }

}