package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryAuthScope(val scopes: List<String>)