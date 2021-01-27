package net.ntworld.sentryIntegration.client.parser.raw

import kotlinx.serialization.Serializable

@Serializable
data class IssueHashRaw(
    val id: String,
    val latestEvent: EventRaw
)