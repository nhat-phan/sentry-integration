package net.ntworld.sentryIntegrationIdea.serviceProvider

import com.intellij.openapi.project.Project
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LocalRepository
import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.editor.EditorManager
import net.ntworld.sentryIntegrationIdea.repository.RepositoryManager

interface ProjectServiceProvider {
    val applicationServiceProvider: ApplicationServiceProvider

    val project: Project

    val connections: List<Connection>
        get() = applicationServiceProvider.connections

    val linkedProjects: List<LinkedProject>

    val pluginConfiguration: PluginConfiguration

    val editorManager: EditorManager

    val repositoryManager: RepositoryManager

    val configurableDisplayName: String

    fun applyPluginConfiguration(config: PluginConfiguration)

    fun getLocalRepositories(): List<LocalRepository>

    fun addLinkedProject(
        name: String,
        connection: Connection,
        environmentName: String,
        sentryProject: SentryProject,
        sentryRootPath: String,
        localRootPath: String,
        deployedBranch: String,
        useCompiledLanguage: Boolean,
        enableWorker: Boolean
    )

    fun updateLinkedProjectsState(projects: List<LinkedProject>)

    fun updateLinkedProject(project: LinkedProject)

    fun deleteLinkedProjects(projects: List<LinkedProject>)

    fun getOpeningProjectIds(): List<String>

    fun setLinkedProjectAsOpened(id: String)

    fun setLinkedProjectAsClosed(id: String)

    fun isLicenced(): Boolean
}