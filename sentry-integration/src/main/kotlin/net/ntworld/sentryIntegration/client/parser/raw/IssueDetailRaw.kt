package net.ntworld.sentryIntegration.client.parser.raw

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class IssueDetailRaw(
    val id: String,

    val assignedTo: JsonObject?,

    val stats: IssueDetailStatsRaw
)