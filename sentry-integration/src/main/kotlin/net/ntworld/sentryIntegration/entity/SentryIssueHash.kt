package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryIssueHash(
    val id: String,
    val latestEventDetail: SentryEventDetail
)