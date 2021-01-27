package net.ntworld.sentryIntegration.storage.data

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.Storage

data class FrameLinkData(
    override val linkedProjectId: String,
    override val issueId: String,
    override val exceptionId: String,
    override val path: String,
    override val index: Int,
    override val line: Int
): Storage.FrameLink