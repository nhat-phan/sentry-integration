package net.ntworld.sentryIntegration.storage.data

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.entity.LocalPath

@Serializable
internal data class IssueData(
    override val id: String,
    override val title: String,
    override val culprit: LocalPath,
    override val permalink: String,
    override val firstSeen: String,
    override val lastSeen: String
): Storage.ReportedIssue