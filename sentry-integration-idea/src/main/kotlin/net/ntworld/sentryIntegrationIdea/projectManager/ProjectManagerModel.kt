package net.ntworld.sentryIntegrationIdea.projectManager

import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.LocalRepository
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.Model
import java.util.*

interface ProjectManagerModel: Model<ProjectManagerModel.DataListener> {
    val localRepositories: List<LocalRepository>

    val connections: List<Connection>

    var linkedProjects: List<LinkedProject>

    fun validateAddProjectFormData(data: AddProjectFormData): Pair<Boolean, String>

    interface DataListener : EventListener {

        fun whenLinkedProjectsDataChanged()

    }

    interface AddProjectFormData {
        val repository: LocalRepository?
        val name: String
        val connection: Connection?
        val sentryProject: SentryProject?
        val environmentName: String
        val environmentRootPath: String
        val deployedBranch: String
        val enableWorker: Boolean
    }

    interface UpsertEnvironmentFormData {
        val name: String
        val connection: Connection?
        val sentryProject: SentryProject?
        val environmentName: String
        val environmentRootPath: String
        val deployedBranch: String
        val enableWorker: Boolean
    }
}