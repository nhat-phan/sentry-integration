package net.ntworld.sentryIntegration.client.response

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.entity.SentryAuthScope
import net.ntworld.sentryIntegration.entity.SentryUserInfo

@Serializable
data class GetCurrentUserResponse(
    val version: String,
    val auth: SentryAuthScope?,
    val user: SentryUserInfo?
)