package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryEventTag(
    val key: String,
    val value: String
)
