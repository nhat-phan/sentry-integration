package net.ntworld.sentryIntegration.entity

import java.nio.file.Paths
import java.util.*

data class LinkedProject(
    val id: String,
    val name: String,
    val connectionId: String,
    val connectionUrl: String,
    val connectionToken: String,
    val environmentName: String,
    val sentryProjectId: String,
    val sentryProjectSlug: String,
    val sentryOrganizationId: String,
    val sentryOrganizationSlug: String,
    val sentryRootPath: String,
    val localRootPath: String,
    val deployedBranch: String,
    val cacheDirectory: String,
    val workspaceDirectory: String?,
    val enableWorker: Boolean,
    val connectionScope: Scope = Scope(null),
    val state: State = State.INITIALIZE
) {

    val connection = Connection(connectionId, connectionUrl, connectionToken)

    val cacheBasePath by lazy {
        val osName = System.getProperty("os.name")
        val isWindow = osName.toLowerCase(Locale.ENGLISH).startsWith("windows")
        val path = if (cacheDirectory.startsWith("~/")) {
            val workspaceDirectory = if (null !== workspaceDirectory) workspaceDirectory else localRootPath
            Paths.get(workspaceDirectory, cacheDirectory.substring(1), id).toString()
        } else {
            Paths.get(cacheDirectory, id).toString()
        }
        if (isWindow) { path.replace('/', '\\') } else path
    }

    enum class State {
        INITIALIZE,
        READY,
        INVALID_CONNECTION,
        INVALID_SENTRY_PROJECT,
        INVALID_LOCAL_ROOT_PATH,
        INVALID_SENTRY_ROOT_PATH
    }

    companion object {
        val Empty = LinkedProject(
            id = "",
            name = "",
            connectionId = "",
            connectionToken = "",
            connectionUrl = "",
            environmentName = "",
            sentryProjectId = "",
            sentryProjectSlug = "",
            sentryOrganizationId = "",
            sentryOrganizationSlug = "",
            sentryRootPath = "",
            deployedBranch = "",
            localRootPath = "",
            cacheDirectory = "",
            workspaceDirectory = "",
            enableWorker = false
        )
    }
}