package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.storage.data.FrameData
import net.ntworld.sentryIntegration.storage.data.FrameLinkData
import net.ntworld.sentryIntegration.storage.data.VariableData
import net.ntworld.sentryIntegration.storage.data.ContextData

@Serializable
data class SentryEventExceptionStacktrace(
    val absolutePath: LocalPath,
    val module: String,
    val function: String,
    val lineNumber: Int,
    val context: List<SentryEventExceptionStacktraceContext>,
    var variables: List<SentryEventExceptionStacktraceVariable>
) {
    fun toStorageData(
        linkedProject: LinkedProject,
        issue: SentryIssue,
        exception: SentryEventException,
        next: SentryEventExceptionStacktrace?,
        nextIndex: Int,
        previous: SentryEventExceptionStacktrace?,
        previousIndex: Int,
        index: Int,
        totalCount: Int,
        source: Storage.FrameSource
    ): Storage.Frame {
        val exceptionData = exception.toReportedException()
        val nextLink = if (null === next) null else {
            FrameLinkData(
                linkedProjectId = linkedProject.id,
                issueId = issue.id,
                exceptionId = exceptionData.id,
                path = next.absolutePath.value,
                line = next.lineNumber,
                index = nextIndex
            )
        }
        val previousLink = if (null === previous) null else {
            FrameLinkData(
                linkedProjectId = linkedProject.id,
                issueId = issue.id,
                exceptionId = exceptionData.id,
                path = previous.absolutePath.value,
                line = previous.lineNumber,
                index = previousIndex
            )
        }
        return FrameData(
            linkedProject = linkedProject,
            issueId = issue.id,
            exceptionId = exception.id,
            path = absolutePath.value,
            module = module,
            function = function,
            visibleLine =  lineNumber,
            index = index,
            total = totalCount,
            variables = variables.map {
                VariableData(name = it.name, value = it.value)
            },
            context = context.map {
                ContextData(line = it.lineNumber, content = it.content)
            },
            next = nextLink,
            previous = previousLink,
            source = source
        )
    }
}