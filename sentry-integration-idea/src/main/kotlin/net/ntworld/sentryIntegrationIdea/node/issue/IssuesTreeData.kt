package net.ntworld.sentryIntegrationIdea.node.issue

import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegration.entity.SentryEventDetail
import net.ntworld.sentryIntegration.entity.SentryIssue

data class IssuesTreeData(
    val issues: List<SentryIssue>,
    val eventDetailsMap: Map<String, SentryEventDetail>,
    val displayEventCount: Boolean
)