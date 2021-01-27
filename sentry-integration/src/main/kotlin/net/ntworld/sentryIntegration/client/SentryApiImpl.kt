package net.ntworld.sentryIntegration.client

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import net.ntworld.sentryIntegration.SentryApi
import net.ntworld.sentryIntegration.SentryApiException
import net.ntworld.sentryIntegration.client.parser.SentryIssueParser
import net.ntworld.sentryIntegration.client.response.GetCurrentUserResponse
import net.ntworld.sentryIntegration.debug
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.Scope
import net.ntworld.sentryIntegration.entity.SentryCustomer
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
import java.net.URLEncoder

internal class SentryApiImpl(
    private val http: HttpClient,
    private val linkedProject: LinkedProject
): SentryApi {
    private val json = Json(JsonConfiguration.Stable.copy(strictMode = false))
    private val mySentryIssueParser = SentryIssueParser(linkedProject)

    override fun getCurrentUser(): Pair<SentryUserInfo, Scope> {
        val content = http.get("/")

        val response = json.parse(GetCurrentUserResponse.serializer(), content)
        if (null === response.user) {
            throw SentryApiException("Cannot be authorized")
        }
        return Pair(response.user, Scope(response.auth))
    }

    override fun getAllProjects(): List<SentryProject> {
        val content = http.get("/projects/")

        return json.parse(SentryProject.serializer().list, content)
    }

    private fun assertOrganizationSlugAndProjectSlugIsSet() {
        if (linkedProject.sentryOrganizationSlug.isEmpty() || linkedProject.sentryProjectSlug.isEmpty()) {
            throw Exception("please use SentryApiManager.make(LinkedProject) factory method")
        }
    }

    override fun getProject(): SentryProject {
        assertOrganizationSlugAndProjectSlugIsSet()
        val content = http.get("/projects/${linkedProject.sentryOrganizationSlug}/${linkedProject.sentryProjectSlug}/")

        return json.parse(SentryProject.serializer(), content)
    }

    override fun getTeams(): List<SentryTeam> {
        assertOrganizationSlugAndProjectSlugIsSet()

        val content = http.get("/organizations/${linkedProject.sentryOrganizationSlug}/teams/")

        return json.parse(SentryTeam.serializer().list, content)
    }

    override fun getUsers(): List<SentryUser> {
        assertOrganizationSlugAndProjectSlugIsSet()

        val content = http.get("/organizations/${linkedProject.sentryOrganizationSlug}/users/")

        return json.parse(SentryUser.serializer().list, content)
    }

    override fun isUsingFreeTier(): Boolean {
        assertOrganizationSlugAndProjectSlugIsSet()
        val content = http.get("/customers/${linkedProject.sentryOrganizationSlug}/")

        return try {
            val customer = json.parse(SentryCustomer.serializer(), content)

            customer.isFree
        } catch (exception: Exception) {
            false
        }
    }

    override fun getIssues(query: String, sortedBy: SentryIssueSortedByEnum): List<SentryIssue> {
        assertOrganizationSlugAndProjectSlugIsSet()

        val url = mutableListOf(
            "/projects/${linkedProject.sentryOrganizationSlug}/${linkedProject.sentryProjectSlug}/issues/",
            "?",
            "statsPeriod=&",
            "shortIdLookup=false&",
            "query=",
            URLEncoder.encode(query, "utf-8")
        )

        when (sortedBy) {
            SentryIssueSortedByEnum.Priority -> { url.add("&sort=priority") }
            SentryIssueSortedByEnum.LastSeen -> {}
            SentryIssueSortedByEnum.FirstSeen -> { url.add("&sort=new") }
            SentryIssueSortedByEnum.Events -> { url.add("&sort=freq") }
            SentryIssueSortedByEnum.Users -> { url.add("&sort=user") }
        }

        val content = http.get(url.joinToString(""))

        return mySentryIssueParser.parseCollection(content)
    }

    override fun getIssueEvents(issueId: String): List<SentryEvent> {
        val content = http.get("/issues/${issueId}/events/")

        return json.parse(SentryEvent.serializer().list, content)
    }

    override fun getIssueDetail(issueId: String): SentryIssueDetail {
        val url = "/issues/${issueId}/"
        val content = http.get(url)

        return mySentryIssueParser.parseIssueDetail(content)
    }

    override fun getIssueHashes(issueId: String): List<SentryIssueHash> {
        val url = "/issues/${issueId}/hashes/"
        val content = http.get(url)

        return mySentryIssueParser.parseHashes(issueId, content)
    }

    override fun mutateIssues(issues: List<SentryIssue>, params: SentryIssueMutateParams) {
        val ids = mutableListOf<String>()
        for (issue in issues) {
            ids.add("id=" + URLEncoder.encode(issue.id, "utf-8"))
        }

        if (ids.count() > 0 && params.isValid()) {
            val url = "/projects/${linkedProject.sentryOrganizationSlug}/${linkedProject.sentryProjectSlug}/issues/"

            val content = http.put(url + "?" + ids.joinToString("&"), params.toJson())

            debug(content)
        }
    }

}