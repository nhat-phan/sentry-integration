package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryUser(
    val user: SentryUserInfo,
    val projects: List<String>,
    val pending: Boolean
)
