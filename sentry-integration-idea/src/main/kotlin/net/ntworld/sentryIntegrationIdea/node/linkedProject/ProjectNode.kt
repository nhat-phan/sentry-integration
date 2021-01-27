package net.ntworld.sentryIntegrationIdea.node.linkedProject

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.node.AbstractNode
import net.ntworld.sentryIntegrationIdea.util.TextChoiceUtil

class ProjectNode(val name: String, val linkedProjects: List<LinkedProject>) : AbstractNode() {
    override val id: String = name

    override fun updatePresentation(presentation: PresentationData) {
        if (isCombinedEnvironment()) {
            updateCombinedEnvPresentation(presentation)
        } else {
            updateProjectPresentation(presentation)
        }
    }

    private fun updateProjectPresentation(presentation: PresentationData) {
        presentation.addText(name, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
        presentation.addText(" · " + TextChoiceUtil.environment(linkedProjects.count()), SimpleTextAttributes.GRAYED_ATTRIBUTES)

        when(getState()) {
            State.INITIALIZING -> presentation.setIcon(AllIcons.Actions.Refresh)
            State.HAS_INVALID -> presentation.setIcon(AllIcons.General.Warning)
            State.ALL_READY -> presentation.setIcon(AllIcons.Nodes.ModuleGroup)
        }
    }

    private fun updateCombinedEnvPresentation(presentation: PresentationData) {
        presentation.addText(name, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
        presentation.addText(" · " + linkedProjects.first().environmentName, SimpleTextAttributes.GRAYED_ATTRIBUTES)

        when(getState()) {
            State.INITIALIZING -> presentation.setIcon(AllIcons.Actions.Refresh)
            State.HAS_INVALID -> presentation.setIcon(AllIcons.General.Error)
            State.ALL_READY -> presentation.setIcon(AllIcons.Nodes.Module)
        }
    }

    fun isCombinedEnvironment(): Boolean = linkedProjects.isNotEmpty() && linkedProjects.count() == 1

    private enum class State {
        INITIALIZING,
        HAS_INVALID,
        ALL_READY
    }

    private fun getState(): State {
        for (item in linkedProjects) {
            if (item.state == LinkedProject.State.INITIALIZE) {
                return State.INITIALIZING
            }
            if (item.state == LinkedProject.State.INVALID_SENTRY_PROJECT) {
                return State.HAS_INVALID
            }
            if (item.state == LinkedProject.State.INVALID_LOCAL_ROOT_PATH) {
                return State.HAS_INVALID
            }
            if (item.state == LinkedProject.State.INVALID_CONNECTION) {
                return State.HAS_INVALID
            }
            if (item.state == LinkedProject.State.INVALID_SENTRY_ROOT_PATH) {
                return State.HAS_INVALID
            }
        }
        return State.ALL_READY
    }
}