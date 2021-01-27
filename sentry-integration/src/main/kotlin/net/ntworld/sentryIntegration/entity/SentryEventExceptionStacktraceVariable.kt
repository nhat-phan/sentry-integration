package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryEventExceptionStacktraceVariable(val name: String, val value: String)
