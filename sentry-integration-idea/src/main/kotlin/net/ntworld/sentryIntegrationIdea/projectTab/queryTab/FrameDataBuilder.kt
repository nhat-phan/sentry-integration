package net.ntworld.sentryIntegrationIdea.projectTab.queryTab

import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryEventException
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace
import net.ntworld.sentryIntegration.entity.SentryIssue

object FrameDataBuilder {

    fun build(
        linkedProject: LinkedProject,
        issue: SentryIssue,
        exception: SentryEventException,
        stacktrace: SentryEventExceptionStacktrace,
        index: Int,
        source: Storage.FrameSource
    ): Storage.Frame {
        val next = if (index == exception.stacktrace.lastIndex) Pair(null, -1) else Pair(exception.stacktrace[index + 1], index + 1)
        val previous = if (index == 0) Pair(null, -1) else Pair(exception.stacktrace[index - 1], index - 1)

        return stacktrace.toStorageData(
            linkedProject,
            issue,
            exception,
            next.first,
            next.second,
            previous.first,
            previous.second,
            index,
            exception.stacktrace.count(),
            source
        )
    }
}