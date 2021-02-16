package net.ntworld.sentryIntegration.storage.data

import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.entity.LinkedProject

data class FrameData(
    override val linkedProject: LinkedProject,
    override val issueId: String,
    override val exceptionId: String,
    override val path: String,
    override val module: String,
    override val function: String,
    override val visibleLine: Int,
    override val index: Int,
    override val total: Int,
    override val variables: List<Storage.Variable>,
    override val context: List<Storage.Context>,
    override val next: Storage.FrameLink?,
    override val previous: Storage.FrameLink?,
    override val source: Storage.FrameSource
): Storage.Frame