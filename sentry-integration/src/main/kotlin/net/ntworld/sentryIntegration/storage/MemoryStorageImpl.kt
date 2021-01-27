package net.ntworld.sentryIntegration.storage

import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.storage.data.FrameData
import net.ntworld.sentryIntegration.storage.data.FrameLinkData
import java.util.*

internal class MemoryStorageImpl(
    private val linkedProject: LinkedProject
) : Storage {
    private val myIssueMap = Collections.synchronizedMap(mutableMapOf<String, Storage.ReportedIssue>())
    private val myExceptionMap = Collections.synchronizedMap(mutableMapOf<String, Storage.ReportedException>())

    @Synchronized
    override fun store(issue: Storage.ReportedIssue, exceptions: List<Storage.ReportedException>) {
        myIssueMap[issue.id] = issue
        for (exception in exceptions) {
            myExceptionMap[exception.id] = exception
        }
    }

    @Synchronized
    override fun findFirstFrame(frame: Storage.Frame): Storage.Frame? {
        val exception = myExceptionMap[frame.exceptionId]
        if (null === exception || exception.stacktrace.isEmpty()) {
            return null
        }

        val index = exception.stacktrace.lastIndex
        return buildFrame(index, frame.issueId, exception, frame.source)
    }

    @Synchronized
    override fun findFrameByLink(frameLink: Storage.FrameLink, source: Storage.FrameSource): Storage.Frame? {
        val exception = myExceptionMap[frameLink.exceptionId]
        if (null === exception) {
            return null
        }

        val index = frameLink.index
        if (index < 0 || index > exception.stacktrace.lastIndex) {
            return null
        }

        val stacktrace = exception.stacktrace[index]
        if (stacktrace.path != frameLink.path || stacktrace.line != frameLink.line) {
            return null
        }
        return buildFrame(index, frameLink.issueId, exception, source)
    }

    private fun buildFrame(index: Int, issueId: String, exception: Storage.ReportedException, source: Storage.FrameSource): Storage.Frame? {
        val stacktrace = exception.stacktrace[index]

        val next = if (index == exception.stacktrace.lastIndex) Pair(null, -1) else Pair(exception.stacktrace[index + 1], index + 1)
        val previous = if (index == 0) Pair(null, -1) else Pair(exception.stacktrace[index - 1], index - 1)
        val nextLink = if (null === next.first) null else {
            FrameLinkData(
                linkedProjectId = linkedProject.id,
                issueId = issueId,
                exceptionId = exception.id,
                path = next.first!!.path,
                line = next.first!!.line,
                index = next.second
            )
        }
        val previousLink = if (null === previous.first) null else {
            FrameLinkData(
                linkedProjectId = linkedProject.id,
                issueId = issueId,
                exceptionId = exception.id,
                path = previous.first!!.path,
                line = previous.first!!.line,
                index = previous.second
            )
        }
        return FrameData(
            linkedProject = linkedProject,
            issueId = issueId,
            exceptionId = exception.id,
            path =  stacktrace.path,
            visibleLine =  stacktrace.line,
            index = index,
            total = exception.stacktrace.count(),
            variables = stacktrace.variables,
            context = stacktrace.contexts,
            next = nextLink,
            previous = previousLink,
            source = source
        )
    }

    @Synchronized
    override fun findIssueById(issueId: String): Storage.ReportedIssue? {
        return myIssueMap[issueId]
    }

}