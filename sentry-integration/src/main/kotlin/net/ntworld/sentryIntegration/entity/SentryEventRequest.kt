package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryEventRequest(
    val method: String,
    val url: String
)