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

class StacktraceFrameWithSourceCodeNode(
    override val issue: SentryIssue,
    override val exception: SentryEventException,
    override val stacktrace: SentryEventExceptionStacktrace,
    override val index: Int,
    val totalCount: Int,
    private val exceptionIndex: Int,
    private val pluginConfiguration: PluginConfiguration
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
            return updatePresentation(presentation)
        }

        presentation.addText(
            "#${totalCount - index - 1} ${findPath(stacktrace.absolutePath.value)}",
            SimpleTextAttributes.REGULAR_ATTRIBUTES
        )
        if (stacktrace.function.isNotEmpty()) {
            presentation.addText(" in ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            presentation.addText(stacktrace.function, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
        if (stacktrace.lineNumber > 0) {
            presentation.addText(" at line ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            presentation.addText(stacktrace.lineNumber.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
        if (pluginConfiguration.showSourceCodeOnStacktraceNode) {
            presentation.addText(" · " + findDisplayedCode(), SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }
    }

    override fun updatePresentation(presentation: PresentationData) {
        val text = mutableListOf(
            "#${totalCount - index - 1} ",
            findPath(stacktrace.absolutePath.value)
        )
        if (stacktrace.function.isNotEmpty()) {
            text.add(" in ")
            text.add(stacktrace.function)
        }
        if (stacktrace.lineNumber > 0) {
            text.add(" at line ")
            text.add(stacktrace.lineNumber.toString())
        }
        if (pluginConfiguration.showSourceCodeOnStacktraceNode) {
            text.add(" · " + findDisplayedCode())
        }
        presentation.addText(text.joinToString(""), SimpleTextAttributes.GRAYED_ATTRIBUTES)
    }

    private fun findPath(input: String): String {
        return if (input.startsWith("/")) {
            input.substring(1)
        } else {
            input
        }
    }

    private fun findDisplayedCode(): String {
        for (context in stacktrace.context) {
            if (context.lineNumber == stacktrace.lineNumber) {
                return context.content.trim()
            }
        }
        return ""
    }
}