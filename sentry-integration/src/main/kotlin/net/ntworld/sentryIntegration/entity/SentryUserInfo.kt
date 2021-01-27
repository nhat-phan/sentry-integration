package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryUserInfo(
    val id: String,
    val name: String,
    val username: String
)