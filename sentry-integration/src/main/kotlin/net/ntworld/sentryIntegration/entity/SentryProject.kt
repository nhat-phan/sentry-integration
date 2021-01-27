package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryProject(
    val id: String,
    val slug: String,
    val name: String,
    val platform: String?,
    val status: String,
    val organization: SentryOrganization
)

