package net.ntworld.sentryIntegration.client.parser.raw

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssueDetailStatsRaw(
    @SerialName("24h")
    val twentyFourHours: List<List<Int>>,

    @SerialName("30d")
    val thirtyDays: List<List<Int>>
)