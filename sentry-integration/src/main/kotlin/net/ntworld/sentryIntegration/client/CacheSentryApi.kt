package net.ntworld.sentryIntegration.client

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import net.ntworld.sentryIntegration.SentryApi
import net.ntworld.sentryIntegration.cache.Cache
import net.ntworld.sentryIntegration.entity.SentryEvent
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryIssueHash
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser

class CacheSentryApi(private val api: SentryApi, private val cache: Cache): SentryApiDecorator(api) {
    private val TTL_ISSUE_EVENTS = 300
    private val TTL_ISSUE_ISSUE_DETAIL = 60
    private val TTL_ISSUE_EVENT_DETAIL = 300
    private val TTL_GET_TEAMS_AND_USERS = 600
    private val json = Json(JsonConfiguration.Stable.copy(strictMode = false, prettyPrint = true))

    override fun getIssueEvents(issueId: String): List<SentryEvent> {
        val key = "getIssueEvents:${issueId}"
        val cached = cache.get(key)
        return if (null === cached) {
            val events = api.getIssueEvents(issueId)

            cache.set(key, json.stringify(SentryEvent.serializer().list, events), TTL_ISSUE_EVENTS)
            events
        } else {
            json.parse(SentryEvent.serializer().list, cached)
        }
    }

    override fun getIssueDetail(issueId: String): SentryIssueDetail {
        val key = "getIssueDetail:${issueId}"
        val cached = cache.get(key)
        return if (null === cached) {
            val detail = api.getIssueDetail(issueId)

            cache.set(key, json.stringify(SentryIssueDetail.serializer(), detail), TTL_ISSUE_ISSUE_DETAIL)
            detail
        } else {
            json.parse(SentryIssueDetail.serializer(), cached)
        }
    }

    override fun getIssueHashes(issueId: String): List<SentryIssueHash> {
        // Consider using issue id because event id can be changed frequently
        val key = "getIssueHashes:${issueId}"
        val cached = cache.get(key)
        return if (null === cached) {
            val hashes = api.getIssueHashes(issueId)

            cache.set(key, json.stringify(SentryIssueHash.serializer().list, hashes), TTL_ISSUE_EVENT_DETAIL)
            hashes
        } else {
            json.parse(SentryIssueHash.serializer().list, cached)
        }
    }

    override fun getTeams(): List<SentryTeam> {
        val key = "getTeams"
        val cached = cache.get(key)

        return if (null === cached) {
            val teams = api.getTeams()

            cache.set(key, json.stringify(SentryTeam.serializer().list, teams), TTL_GET_TEAMS_AND_USERS)
            teams
        } else {
            json.parse(SentryTeam.serializer().list, cached)
        }
    }

    override fun getUsers(): List<SentryUser> {
        val key = "getUsers"
        val cached = cache.get(key)

        return if (null === cached) {
            val users = api.getUsers()

            cache.set(key, json.stringify(SentryUser.serializer().list, users), TTL_GET_TEAMS_AND_USERS)
            users
        } else {
            json.parse(SentryUser.serializer().list, cached)
        }
    }
}