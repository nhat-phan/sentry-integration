package net.ntworld.sentryIntegration.client.parser

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.list
import net.ntworld.sentryIntegration.SentryApiParsedException
import net.ntworld.sentryIntegration.client.parser.raw.IssueCollectionRaw
import net.ntworld.sentryIntegration.client.parser.raw.IssueDetailRaw
import net.ntworld.sentryIntegration.client.parser.raw.IssueDetailStatsRaw
import net.ntworld.sentryIntegration.client.parser.raw.IssueHashRaw
import net.ntworld.sentryIntegration.entity.DateTime
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.LocalPath
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegration.entity.SentryIssueAssignee
import net.ntworld.sentryIntegration.entity.SentryIssueDetail
import net.ntworld.sentryIntegration.entity.SentryIssueHash
import net.ntworld.sentryIntegration.entity.SentryIssueStat
import net.ntworld.sentryIntegration.makeErrorLevel

class SentryIssueParser(
    private val linkedProject: LinkedProject
) {
    private val json = Json(JsonConfiguration.Stable.copy(strictMode = false))
    private val mySentryEventParser = SentryEventParser(linkedProject)

    fun parseCollection(content: String): List<SentryIssue> {
        return doParseCollection(content).map {
            convertIssueCollectionRawToSentryIssue(it)
        }
    }

    fun parseHashes(issueId: String, content: String): List<SentryIssueHash> {
        val result = json.parse(IssueHashRaw.serializer().list, content)
        return result.map {
            SentryIssueHash(
                id = it.id,
                latestEventDetail = mySentryEventParser.transformEventRaw(issueId, it.latestEvent)
            )
        }
    }

    fun parseIssueDetail(content: String): SentryIssueDetail {
        val raw = json.parse(IssueDetailRaw.serializer(), content)
        return SentryIssueDetail(
            id = raw.id,
            assignedTo = convertAssignedTo(raw.assignedTo),
            twentyFourHoursStat = convertStat(SentryIssueStat.Type.TWENTY_FOUR_HOURS, raw.stats.twentyFourHours),
            thirtyDaysStat = convertStat(SentryIssueStat.Type.THIRTY_DAYS, raw.stats.thirtyDays)
        )
    }

    private fun convertStat(type: SentryIssueStat.Type, values: List<List<Int>>): SentryIssueStat {
        val items = mutableListOf<SentryIssueStat.Item>()
        for (i in 0..values.lastIndex) {
            val start = values[i][0].toLong()
            val end = if (i == values.lastIndex) {
                if (type == SentryIssueStat.Type.TWENTY_FOUR_HOURS) {
                    values[i][0].toLong() + 3600
                } else {
                    values[i][0].toLong() + 86400
                }
            } else {
                values[i+1][0].toLong()
            }
            items.add(SentryIssueStat.Item(start = start, end = end, count = values[i][1]))
        }
        return SentryIssueStat(type, items)
    }

    private fun doParseCollection(content: String): List<IssueCollectionRaw> = try {
        json.parse(IssueCollectionRaw.serializer().list, content)
    } catch (exception: Exception) {
        throw SentryApiParsedException("GET: issues", content, exception.message)
    }

    private fun convertIssueCollectionRawToSentryIssue(input: IssueCollectionRaw): SentryIssue {
        val countRaw = input.count.toIntOrNull()
        return SentryIssue(
            id = input.id,
            title = input.title,
            culprit = LocalPath.create(input.culprit, linkedProject.sentryRootPath),
            permalink = input.permalink,
            firstSeen = DateTime(input.firstSeen),
            lastSeen = DateTime(input.lastSeen),
            hasSeen = input.hasSeen,
            isBookmarked = input.isBookmarked,
            isSubscribed = input.isSubscribed,
            level = makeErrorLevel(input.level),
            status = input.status,
            userCount = input.userCount,
            assignedTo = convertAssignedTo(input.assignedTo),
            count = if (null === countRaw) 0 else countRaw
        )
    }

    private fun convertAssignedTo(input: JsonObject?): SentryIssueAssignee? {
        if (null === input) {
            return null
        }

        return SentryIssueAssignee(
            id = input["id"]!!.primitive.content,
            name = input["name"]!!.primitive.content,
            type = input["type"]!!.primitive.content
        )
    }
}