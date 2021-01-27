package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryOrganization(
    val id: String,
    val slug: String,
    val name: String
)