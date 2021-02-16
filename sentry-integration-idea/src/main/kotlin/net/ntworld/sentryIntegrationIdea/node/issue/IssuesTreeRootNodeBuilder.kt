package net.ntworld.sentryIntegrationIdea.node.issue

import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegrationIdea.node.RootNode
import net.ntworld.sentryIntegrationIdea.node.RootNodeBuilder
import net.ntworld.sentryIntegrationIdea.util.EventTagsUtil

class IssuesTreeRootNodeBuilder(
    private val data: IssuesTreeData,
    private val linkedProject: LinkedProject,
    private val pluginConfiguration: PluginConfiguration
): RootNodeBuilder {

    override fun build(): RootNode {
        val root = RootNode()
        for (issue in data.issues) {
            val issueNode = IssueNode(issue, data.displayEventCount, pluginConfiguration)

            if (pluginConfiguration.displayCulpritNode) {
                val culpritNode = CulpritNode(issue)
                issueNode.add(culpritNode)
            }

            val seenNode = SeenTimeNode(issue)
            issueNode.add(seenNode)

            val detail = data.eventDetailsMap[issue.id]
            if (null === detail) {
                val detailLoadingNode = DetailLoadingNode(issue.id)
                issueNode.add(detailLoadingNode)
            } else {
                val tags = EventTagsUtil.getHighlightTags(pluginConfiguration, detail.tags)
                if (tags.isNotEmpty()) {
                    val tagsNode = TagsNode(issue, tags)
                    issueNode.add(tagsNode)
                }

                for (i in 0..detail.exceptions.lastIndex) {
                    val exception = detail.exceptions[i]
                    val exceptionNode = ExceptionNode(issue, exception, i)

                    val totalCount = exception.stacktrace.count()
                    for (j in exception.stacktrace.lastIndex downTo 0) {
                        val stacktraceNode: StacktraceFrameNode = if (linkedProject.useCompiledLanguage) {
                            StacktraceFrameCompiledLanguageNode(
                                issue = issue,
                                exception = exception,
                                stacktrace = exception.stacktrace[j],
                                index = j,
                                totalCount = totalCount,
                                exceptionIndex = i
                            )
                        } else {
                            StacktraceFrameWithSourceCodeNode(
                                issue = issue,
                                exception = exception,
                                stacktrace = exception.stacktrace[j],
                                index = j,
                                totalCount = totalCount,
                                exceptionIndex = i,
                                pluginConfiguration = pluginConfiguration
                            )
                        }
                        exceptionNode.add(stacktraceNode)
                    }

                    issueNode.add(exceptionNode)
                }
            }

            root.add(issueNode)
        }
        return root
    }
}