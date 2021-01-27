package net.ntworld.sentryIntegrationIdea.projectManager

import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.LocalRepository
import net.ntworld.sentryIntegrationIdea.AbstractModel
import net.ntworld.sentryIntegrationIdea.notifier.LinkedProjectNotifier
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import net.ntworld.sentryIntegrationIdea.util.ModelUtil

class ProjectManagerModelImpl(
    private val projectServiceProvider: ProjectServiceProvider
): AbstractModel<ProjectManagerModel.DataListener>(), ProjectManagerModel {
    private val myLinkedProjects = mutableMapOf<String, LinkedProject>()
    // TODO: dispose message bus connection?
    private val messageBusConnection = projectServiceProvider.project.messageBus.connect()
    private val myLinkedProjectNotifier = object : LinkedProjectNotifier {
        override fun linkedProjectsChanged(linkedProjects: List<LinkedProject>) {
            this@ProjectManagerModelImpl.linkedProjects = linkedProjects
        }
    }
    override val dispatcher = EventDispatcher.create(ProjectManagerModel.DataListener::class.java)

    override val localRepositories: List<LocalRepository>
        get() = projectServiceProvider.getLocalRepositories()

    override val connections: List<Connection>
        get() = projectServiceProvider.connections

    override var linkedProjects: List<LinkedProject>
        get() = myLinkedProjects.values.toList()
        set(value) {
            if (ModelUtil.isAnyProjectChangedInMap(value, myLinkedProjects)) {
                ModelUtil.copyProjectsToMap(value, myLinkedProjects)
                dispatcher.multicaster.whenLinkedProjectsDataChanged()
            }
        }

    init {
        messageBusConnection.subscribe(LinkedProjectNotifier.TOPIC, myLinkedProjectNotifier)
    }

    override fun validateAddProjectFormData(data: ProjectManagerModel.AddProjectFormData): Pair<Boolean, String> {
        val missingFieldPair = checkAddProjectFormDataRequiredData(data)
        if (!missingFieldPair.first) {
            return missingFieldPair
        }

        for (project in myLinkedProjects.values) {
            if (project.name != data.name) {
                continue
            }

            val repository = data.repository
            if (null !== repository && project.localRootPath != repository.path) {
                return Pair(false, "The project \"${data.name}\" already exists, please choose another name.")
            }

            if (project.environmentName == data.environmentName) {
                return Pair(false, "The environment \"${data.environmentName}\" of project \"${data.name}\" already exists, please choose another name.")
            }
        }
        return Pair(true, "")
    }

    private fun checkAddProjectFormDataRequiredData(data: ProjectManagerModel.AddProjectFormData): Pair<Boolean, String> {
        val missingFields = mutableListOf<String>()

        if (data.name.isEmpty()) {
            missingFields.add("project's name")
        }

        if (data.environmentName.isEmpty()) {
            missingFields.add("environment's name")
        }

        val repository = data.repository
        if (null === repository || repository.path.isEmpty()) {
            missingFields.add("repository")
        }

        val connection = data.connection
        if (null === connection) {
            missingFields.add("connection")
        }

        val sentryProject = data.sentryProject
        if (null === sentryProject) {
            missingFields.add("sentry project")
        }

        if (missingFields.isEmpty()) {
            return Pair(true, "")
        }
        return Pair(false, "Please fill ${missingFields.joinToString(", ")}")
    }
}