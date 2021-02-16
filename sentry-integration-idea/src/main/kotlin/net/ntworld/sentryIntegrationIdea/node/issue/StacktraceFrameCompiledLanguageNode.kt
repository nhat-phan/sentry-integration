package net.ntworld.sentryIntegrationIdea.node.issue

import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegration.entity.SentryEventException
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.node.AbstractNode
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class StacktraceFrameCompiledLanguageNode(
    override val issue: SentryIssue,
    override val exception: SentryEventException,
    override val stacktrace: SentryEventExceptionStacktrace,
    override val index: Int,
    val totalCount: Int,
    private val exceptionIndex: Int
): AbstractNode(), StacktraceFrameNode {
    override val id: String = "StacktraceFrameNode:${issue.id}:$exceptionIndex:$index"

    override fun updatePresentation(
        projectServiceProvider: ProjectServiceProvider,
        linkedProject: LinkedProject?,
        presentation: PresentationData
    ) {
        if (null === linkedProject) {
            return updatePresentation(presentation)
        }

        val repositoryManager = projectServiceProvider.makeRepositoryManager(linkedProject)
        if (!repositoryManager.isSourceFile(linkedProject, stacktrace)) {
            return addPresentationTexts(presentation, SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }

        return updatePresentation(presentation)
    }

    override fun updatePresentation(presentation: PresentationData) {
        addPresentationTexts(presentation, SimpleTextAttributes.REGULAR_ATTRIBUTES)
    }

    private fun addPresentationTexts(presentation: PresentationData, mainTextAttributes: SimpleTextAttributes) {
        presentation.addText(
            "#${totalCount - index - 1} ${stacktrace.module}",
            mainTextAttributes
        )
        if (stacktrace.function.isNotEmpty()) {
            presentation.addText(" in ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            presentation.addText(stacktrace.function, mainTextAttributes)
        }
        if (stacktrace.lineNumber > 0) {
            presentation.addText(" at line ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            presentation.addText(stacktrace.lineNumber.toString(), mainTextAttributes)
        }
    }
}