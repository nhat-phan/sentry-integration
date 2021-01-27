package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryEventExceptionStacktraceContext(val lineNumber: Int, val content: String)
