package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.storage.data.*
import java.util.*

@Serializable
data class SentryEventException(
    val id: String,
    val type: String,
    val value: String,
    val stacktrace: List<SentryEventExceptionStacktrace>
) {
    fun toReportedException(): Storage.ReportedException {
        return ExceptionData(
            id = id,
            type = type,
            value = value,
            stacktrace = stacktrace.map {
                StacktraceFrameData(
                    path = it.absolutePath.value,
                    reportedPath = it.absolutePath.originValue,
                    module = it.module,
                    function = it.function,
                    line = it.lineNumber,
                    contexts = it.context.map {
                        ContextData(line = it.lineNumber, content = it.content)
                    },
                    variables = it.variables.map {
                        VariableData(name = it.name, value = it.value)
                    }
                )
            }
        )
    }
}




