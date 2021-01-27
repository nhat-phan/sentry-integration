package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryIssueAssignee(
    val id: String,
    val name: String,
    val type: String
)
