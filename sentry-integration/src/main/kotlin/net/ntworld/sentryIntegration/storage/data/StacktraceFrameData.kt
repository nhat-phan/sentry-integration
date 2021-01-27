package net.ntworld.sentryIntegration.storage.data

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.Storage

@Serializable
internal data class StacktraceFrameData(
    override val path: String,

    override val reportedPath: String,

    override val line: Int,

    override val contexts: List<Storage.Context>,

    override val variables: List<Storage.Variable>
): Storage.StacktraceFrame