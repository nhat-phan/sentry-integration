package net.ntworld.sentryIntegration.entity

import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.storage.data.IssueData

data class SentryIssue(
    val id: String,
    val title: String,
    val culprit: LocalPath,
    val permalink: String,
    val firstSeen: DateTime,
    val lastSeen: DateTime,
    val hasSeen: Boolean,
    val isBookmarked: Boolean,
    val isSubscribed: Boolean,
    val userCount: Int,
    val count: Int,
    val assignedTo: SentryIssueAssignee?,
    val status: String,
    val level: ErrorLevel
) {
    fun toReportedIssue(): Storage.ReportedIssue = IssueData(
        id = id,
        title = title,
        culprit = culprit,
        permalink = permalink,
        firstSeen = firstSeen.value,
        lastSeen = lastSeen.value
    )
}
