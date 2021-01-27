package net.ntworld.sentryIntegration.client.parser.raw

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/* Schema file: event-schema.json */
@Serializable
data class EventRaw(
    // line 50
    val id: String,

    // line 809
    val groupID: String,

    // line 812
    val title: String,

    // line 32
    val eventID: String,

    // line 47
    val message: String,

    // line 80
    val type: String,

    // line 179
    val entries: List<JsonElement>,

    // line 113
    val tags: List<JsonElement>?,

    // line 137
    // TODO: create a separated object
    // val user: JsonElement?,

    // line 612: schema says not nullable, but let's treat as nullable
    val context: JsonElement?

    // line 83
    // TODO: create a separated object
    // val metadata: JsonElement?
)