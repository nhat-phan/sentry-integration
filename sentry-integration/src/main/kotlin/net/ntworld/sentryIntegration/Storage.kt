package net.ntworld.sentryIntegration

import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.LocalPath

interface Storage {
    fun store(issue: ReportedIssue, exceptions: List<ReportedException>)

    fun findFrameByLink(frameLink: FrameLink, source: FrameSource): Frame?

    fun findFirstFrame(frame: Frame): Frame?

    fun findIssueById(issueId: String): ReportedIssue?

    /**
     * Raw data of an sentry issue from sentry which will be stored to storage
     */
    interface ReportedIssue {
        val id: String

        val title: String

        val culprit: LocalPath

        val permalink: String

        val firstSeen: String

        val lastSeen: String
    }

    /**
     * Raw data of an exception from sentry which will be stored to storage
     */
    interface ReportedException {
        val id: String

        val type: String

        val value: String

        val stacktrace: List<StacktraceFrame>
    }

    /**
     * Raw data of an exception's stacktrace frame from sentry which will be stored to storage
     */
    interface StacktraceFrame {
        val path: String

        val reportedPath: String

        val line: Int

        val contexts: List<Context>

        val variables: List<Variable>
    }

    /**
     * Raw data of a frame's context from sentry which will be stored to storage
     */
    interface Context {
        val line: Int
        val content: String
    }

    /**
     * Raw data of a variable from sentry which will be stored to storage
     */
    interface Variable {
        val name: String
        val value: String
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The data used by editor to jump between Frame, it will be processed from raw data above
     */
    interface FrameLink {
        val linkedProjectId: String

        val issueId: String

        val exceptionId: String

        val path: String

        val line: Int

        val index: Int
    }

    /**
     * The data used by editor to display, it will be processed from raw data above and project information
     */
    interface Frame {
        val id: String
            get() = "${linkedProject.id}/${issueId}/${exceptionId}/$index"

        val linkedProject: LinkedProject

        val issueId: String

        val exceptionId: String

        val path: String

        val visibleLine: Int

        val index: Int

        val total: Int

        val variables: List<Variable>

        val context: List<Context>

        val next: FrameLink?

        val previous: FrameLink?

        val source: FrameSource

        val lastIndex: Int
            get() = total - 1
    }

    enum class FrameSource {
        MAIN_UI,
        WORKER
    }
}