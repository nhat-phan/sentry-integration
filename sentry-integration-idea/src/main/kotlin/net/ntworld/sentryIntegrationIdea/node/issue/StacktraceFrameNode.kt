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

class StacktraceFrameNode(
    val issue: SentryIssue,
    val exception: SentryEventException,
    val stacktrace: SentryEventExceptionStacktrace,
    val index: Int,
    val totalCount: Int,
    private val exceptionIndex: Int,
    private val pluginConfiguration: PluginConfiguration
): AbstractNode() {
    override val id: String = "StacktraceFrameNode:${issue.id}:$exceptionIndex:$index"

    override fun updatePresentation(
        projectServiceProvider: ProjectServiceProvider,
        linkedProject: LinkedProject?,
        presentation: PresentationData
    ) {
        if (null === linkedProject) {
            return updatePresentation(presentation)
        }

        if (!projectServiceProvider.repositoryManager.isSourceFile(linkedProject, stacktrace.absolutePath.value)) {
            return updatePresentation(presentation)
        }

        presentation.addText(
            "#${totalCount - index - 1} ${findPath(stacktrace.absolutePath.value)}",
            SimpleTextAttributes.REGULAR_ATTRIBUTES
        )
        presentation.addText(" · ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        presentation.addText(stacktrace.lineNumber.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
        if (pluginConfiguration.showSourceCodeOnStacktraceNode) {
            presentation.addText(" · " + findDisplayedCode(), SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }
    }

    override fun updatePresentation(presentation: PresentationData) {
        val text = listOf(
            "#${totalCount - index - 1} ",
            findPath(stacktrace.absolutePath.value),
            " · ",
            stacktrace.lineNumber.toString()
        )
        presentation.addText(text.joinToString(""), SimpleTextAttributes.GRAYED_ATTRIBUTES)
        if (pluginConfiguration.showSourceCodeOnStacktraceNode) {
            presentation.addText(" · " + findDisplayedCode(), SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }
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