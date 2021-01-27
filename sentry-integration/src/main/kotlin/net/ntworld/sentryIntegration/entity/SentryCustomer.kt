package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable

@Serializable
data class SentryCustomer(
    val name: String,
    val isFree: Boolean
)