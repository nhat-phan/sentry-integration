package net.ntworld.sentryIntegration.client.parser.raw

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

// Schema file: issues-schema.json
@Serializable
data class IssueCollectionRaw(
    // line 56
    val id: String,

    // line 168
    val title: String,

    // line 47
    val culprit: String,

    // line 115
    val permalink: String,

    // line 50
    val firstSeen: String,

    // line 50
    val lastSeen: String,

    // line 53
    val hasSeen: Boolean,

    // line 59
    val isBookmarked: Boolean,

    // line 65
    val isSubscribed: Boolean,

    // line 71
    val level: String,

    // line 153
    val status: String,

    // line 78, schema says not nullable, but let's treat as nullable
    // skip for now
    // val metadata: JsonElement,

    // line 174
    val userCount: Int,

    // line 44
    val count: String,

    // line 40
    val assignedTo: JsonObject?,

    // edge case: when search by event id, not listed in the schema
    val matchingEventId: String? = null
)
