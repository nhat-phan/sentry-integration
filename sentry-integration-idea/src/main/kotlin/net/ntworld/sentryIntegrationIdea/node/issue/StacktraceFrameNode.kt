package net.ntworld.sentryIntegrationIdea.node.issue

import net.ntworld.sentryIntegration.entity.SentryEventException
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.node.Node

interface StacktraceFrameNode: Node {
    val issue: SentryIssue

    val exception: SentryEventException

    val stacktrace: SentryEventExceptionStacktrace

    val index: Int
}