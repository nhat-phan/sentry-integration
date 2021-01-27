package net.ntworld.sentryIntegration

import net.ntworld.sentryIntegration.entity.Scope
import net.ntworld.sentryIntegration.entity.SentryEvent
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryIssueHash
import net.ntworld.sentryIntegration.entity.SentryIssueMutateParams
import net.ntworld.sentryIntegration.entity.SentryIssueSortedByEnum
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegration.entity.SentryUserInfo

interface SentryApi {
    fun getCurrentUser(): Pair<SentryUserInfo, Scope>

    fun getAllProjects(): List<SentryProject>

    fun getProject(): SentryProject

    fun getTeams(): List<SentryTeam>

    fun getUsers(): List<SentryUser>

    fun isUsingFreeTier(): Boolean

    fun getIssues(query: String, sortedBy: SentryIssueSortedByEnum): List<SentryIssue>

    fun getIssueEvents(issueId: String): List<SentryEvent>

    fun getIssueDetail(issueId: String): SentryIssueDetail

    fun getIssueHashes(issueId: String): List<SentryIssueHash>

    fun mutateIssues(issues: List<SentryIssue>, params: SentryIssueMutateParams)
}