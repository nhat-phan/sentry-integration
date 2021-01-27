package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryTeam(
    val id: String,
    val slug: String,
    val name: String
)
