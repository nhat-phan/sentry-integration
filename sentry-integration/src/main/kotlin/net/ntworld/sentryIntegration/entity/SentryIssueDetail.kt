package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryIssueDetail(
    val id: String,
    val assignedTo: SentryIssueAssignee?,
    val twentyFourHoursStat: SentryIssueStat,
    val thirtyDaysStat: SentryIssueStat
)