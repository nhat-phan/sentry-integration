package net.ntworld.sentryIntegration.storage.data

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.Storage

@Serializable
internal data class VariableData(
    override val name: String,
    override val value: String
): Storage.Variable