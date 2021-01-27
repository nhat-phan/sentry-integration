package net.ntworld.sentryIntegration.client.parser.raw

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// Schema file: issue-schema.json
@Serializable
data class IssueRaw(
    // line 166
    val id: String,

    // line 330
    val title: String,

    // line 79
    val culprit: String,

    // line 235
    val permalink: String,

    // line 160
    val firstSeen: String,

    // line 182
    val lastSeen: String,

    // line 163
    val hasSeen: Boolean,

    // line 169
    val isBookmarked: Boolean,

    // line 175
    val isSubscribed: Boolean,

    // line 185
    val level: String,

    // line 192, schema says not nullable, but let's treat as nullable
    val metadata: JsonElement,

    // line 273
    // TODO: create a SeenByUserRaw
    val seenBy: List<JsonElement>,

    // line 336
    val userCount: Int,

    // line 339
    val userReportCount: Int?
)
