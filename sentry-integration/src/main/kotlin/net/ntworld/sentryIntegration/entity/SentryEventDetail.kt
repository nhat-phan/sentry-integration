package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryEventDetail(
    val id: String,
    val issueId: String,
    val exceptions: List<SentryEventException>,
    val request: SentryEventRequest?,
    val tags: List<SentryEventTag>
)