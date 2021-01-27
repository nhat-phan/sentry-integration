package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentryEvent(
    val id: String,
    @SerialName("eventID")
    val eventId: String,
    val dateCreated: String
    // val tags: List<SentryEventTag>
)
