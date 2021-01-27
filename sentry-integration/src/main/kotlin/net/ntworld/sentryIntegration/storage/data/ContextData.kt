package net.ntworld.sentryIntegration.storage.data

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.Storage

@Serializable
internal data class ContextData(
    override val line: Int,
    override val content: String
): Storage.Context