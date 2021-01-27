package net.ntworld.sentryIntegration.storage.data

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.Storage

@Serializable
internal data class ExceptionData(
    override val id: String,

    override val type: String,

    override val value: String,

    override val stacktrace: List<Storage.StacktraceFrame>
): Storage.ReportedException

