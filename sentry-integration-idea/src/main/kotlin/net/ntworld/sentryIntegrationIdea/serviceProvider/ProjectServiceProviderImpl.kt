package net.ntworld.sentryIntegrationIdea.serviceProvider

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import net.ntworld.sentryIntegration.debug
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.LocalRepository
import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.editor.EditorManager
import net.ntworld.sentryIntegrationIdea.editor.EditorManagerImpl
import net.ntworld.sentryIntegrationIdea.license.LicenseChecker
import net.ntworld.sentryIntegrationIdea.notifier.LinkedProjectNotifier
import net.ntworld.sentryIntegrationIdea.repository.RepositoryManager
import net.ntworld.sentryIntegrationIdea.repository.RepositoryManagerImpl
import org.jdom.Element
import java.util.*

@State(name = "SentryIntegrationProjectLevel", storages = [(Storage("sentry-integration.xml"))])
open class ProjectServiceProviderImpl(
    override val project: Project
) : ProjectServiceProvider, PersistentStateComponent<Element> {
    private val myLinkedProjects = mutableMapOf<String, LinkedProject>()
    private val myOpeningProjectIds = mutableSetOf<String>()
    private var myPluginConfiguration = PluginConfiguration.Default

    override val applicationServiceProvider: ApplicationServiceProvider
        get() {
            return ServiceManager.getService(
                ApplicationServiceProvider::class.java
            )
        }

    override val linkedProjects: List<LinkedProject>
        get() = myLinkedProjects.values.toList()

    override val pluginConfiguration: PluginConfiguration
        get() = myPluginConfiguration

    override val editorManager: EditorManager = EditorManagerImpl(this)

    override val repositoryManager: RepositoryManager = RepositoryManagerImpl(project)

    override val configurableDisplayName: String = "Sentry Integration"

    override fun applyPluginConfiguration(config: PluginConfiguration) {
        myPluginConfiguration = config
        for (project in myLinkedProjects) {
            myLinkedProjects[project.key] = project.value.copy(cacheDirectory = config.cacheDirectory)
        }
        debug("ProjectServiceProvider notify linkedProjectsChanged because applyPluginConfiguration")
        this.project.messageBus.syncPublisher(LinkedProjectNotifier.TOPIC).linkedProjectsChanged(linkedProjects)
    }

    override fun getLocalRepositories(): List<LocalRepository> {
        val vcsRepositoryManager = VcsRepositoryManager.getInstance(project)
        return vcsRepositoryManager.repositories.map {
            LocalRepository(
                name = it.presentableUrl,
                path = it.root.path
            )
        }
    }

    override fun addLinkedProject(
        name: String,
        connection: Connection,
        environmentName: String,
        sentryProject: SentryProject,
        sentryRootPath: String,
        localRootPath: String,
        deployedBranch: String,
        enableWorker: Boolean
    ) {
        val project = LinkedProject(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            connectionId = connection.id,
            connectionUrl = connection.url,
            connectionToken = connection.token,
            environmentName = environmentName,
            sentryProjectId = sentryProject.id,
            sentryProjectSlug = sentryProject.slug,
            sentryOrganizationId = sentryProject.organization.id,
            sentryOrganizationSlug = sentryProject.organization.slug,
            sentryRootPath = sentryRootPath,
            localRootPath = localRootPath,
            deployedBranch = deployedBranch,
            cacheDirectory = pluginConfiguration.cacheDirectory,
            workspaceDirectory = project.basePath,
            enableWorker = false // for now it's always false
        )
        myLinkedProjects[project.id] = project
        debug("ProjectServiceProvider notify linkedProjectsChanged because addLinkedProject")
        this.project.messageBus.syncPublisher(LinkedProjectNotifier.TOPIC).linkedProjectsChanged(linkedProjects)
    }

    override fun updateLinkedProjectsState(projects: List<LinkedProject>) {
        var changed = false
        for (linkedProject in projects) {
            if (myLinkedProjects.containsKey(linkedProject.id)) {
                myLinkedProjects[linkedProject.id] = linkedProject
                changed = true
            }
        }
        if (changed) {
            debug("ProjectServiceProvider notify linkedProjectsChanged because updateLinkedProjectsState")
            this.project.messageBus.syncPublisher(LinkedProjectNotifier.TOPIC).linkedProjectsChanged(linkedProjects)
        }
    }

    override fun updateLinkedProject(project: LinkedProject) {
        if (myLinkedProjects.containsKey(project.id)) {
            myLinkedProjects[project.id] = project
            this.project.messageBus.syncPublisher(LinkedProjectNotifier.TOPIC).linkedProjectsChanged(linkedProjects)
        }
    }

    override fun deleteLinkedProjects(projects: List<LinkedProject>) {
        var changed = false
        for (linkedProject in projects) {
            if (myLinkedProjects.containsKey(linkedProject.id)) {
                myLinkedProjects.remove(linkedProject.id)
                changed = true
            }
        }
        if (changed) {
            debug("ProjectServiceProvider notify linkedProjectsChanged because deleteLinkedProjects")
            this.project.messageBus.syncPublisher(LinkedProjectNotifier.TOPIC).linkedProjectsChanged(linkedProjects)
        }
    }

    override fun getOpeningProjectIds(): List<String> = myOpeningProjectIds.toList()

    override fun setLinkedProjectAsOpened(id: String) { myOpeningProjectIds.add(id) }

    override fun setLinkedProjectAsClosed(id: String) { myOpeningProjectIds.remove(id) }

    override fun isLicenced(): Boolean {
        return LicenseChecker.checkLicense(applicationServiceProvider.isPaidPlugin, linkedProjects)
    }

    override fun getState(): Element? {
        debug("PROJECT: getState() for saving persistence data")
        val element = Element("LinkedProject")

        val config = Element("Configuration")
        config.setAttribute("cacheDirectory", pluginConfiguration.cacheDirectory)
        config.setAttribute("prioritizedTags", pluginConfiguration.prioritizedTags)
        config.setAttribute("markIssueAsSeenAutomatically", if (pluginConfiguration.markIssueAsSeenAutomatically) "1" else "0")
        config.setAttribute("displayCulpritNode", if (pluginConfiguration.displayCulpritNode) "1" else "0")
        config.setAttribute("showEventCountAtTheEndOfIssueNode", if (pluginConfiguration.showEventCountAtTheEndOfIssueNode) "1" else "0")
        config.setAttribute("displayErrorLevelIcon", if (pluginConfiguration.displayErrorLevelIcon) "1" else "0")
        config.setAttribute("grayOutUnsubscribeIssue", if (pluginConfiguration.grayOutUnsubscribeIssue) "1" else "0")
        config.setAttribute("showSourceCodeOnStacktraceNode", if (pluginConfiguration.showSourceCodeOnStacktraceNode) "1" else "0")
        element.addContent(config)

        for (id in myOpeningProjectIds) {
            val item = Element("Opening")
            item.setAttribute("id", id)
            element.addContent(item)
        }

        myLinkedProjects.values.forEach {
            val item = Element("Item")

            item.setAttribute("id", it.id)
            item.setAttribute("name", it.name)
            item.setAttribute("environmentName", it.environmentName)
            item.setAttribute("localRootPath", it.localRootPath)
            item.setAttribute("deployedBranch", it.deployedBranch)
            item.setAttribute("connectionId", it.connectionId)
            item.setAttribute("sentryOrganizationSlug", it.sentryOrganizationSlug)
            item.setAttribute("sentryProjectSlug", it.sentryProjectSlug)
            item.setAttribute("sentryRootPath", it.sentryRootPath)
            // for now it's always false
            item.setAttribute("enableWorker", "0")
            // item.setAttribute("enableWorker", if (it.enableWorker) "1" else "0" )

            element.addContent(item)
        }
        return element
    }

    override fun loadState(state: Element) {
        debug("PROJECT: load persistence data from settings")
        for (item in state.children) {
            if (item.name == "Configuration") {
                myPluginConfiguration = PluginConfiguration(
                    cacheDirectory = readStringAttribute(item, "cacheDirectory", "~/.idea/sentryIntegrationCache/"),
                    prioritizedTags = readStringAttribute(item, "prioritizedTags", "browser,client_os,runtime"),
                    markIssueAsSeenAutomatically = readBooleanAttribute(item, "markIssueAsSeenAutomatically", true),
                    displayCulpritNode = readBooleanAttribute(item, "displayCulpritNode", true),
                    showEventCountAtTheEndOfIssueNode = readBooleanAttribute(item, "showEventCountAtTheEndOfIssueNode", true),
                    displayErrorLevelIcon = readBooleanAttribute(item, "displayErrorLevelIcon", false),
                    grayOutUnsubscribeIssue = readBooleanAttribute(item, "grayOutUnsubscribeIssue", true),
                    showSourceCodeOnStacktraceNode = readBooleanAttribute(item, "showSourceCodeOnStacktraceNode", true)
                )
                continue
            }

            if (item.name == "Opening") {
                val id = readStringAttribute(item, "id", "")
                if (id.isNotEmpty()) {
                    myOpeningProjectIds.add(id)
                }
            }

            if (item.name == "Item") {
                val id = item.getAttribute("id").value
                myLinkedProjects[id] = LinkedProject(
                    id = id,
                    cacheDirectory = pluginConfiguration.cacheDirectory,
                    workspaceDirectory = project.basePath,
                    name = item.getAttribute("name").value,
                    connectionId = item.getAttribute("connectionId").value,
                    connectionUrl = "",
                    connectionToken = "",
                    environmentName = item.getAttribute("environmentName").value,
                    sentryProjectId = "",
                    sentryProjectSlug = item.getAttribute("sentryProjectSlug").value,
                    sentryOrganizationId = "",
                    sentryOrganizationSlug = item.getAttribute("sentryOrganizationSlug").value,
                    sentryRootPath = item.getAttribute("sentryRootPath").value,
                    localRootPath = item.getAttribute("localRootPath").value,
                    deployedBranch = item.getAttribute("deployedBranch").value,
                    // for now it's always false
                    enableWorker = false,
                    // enableWorker = item.getAttribute("enableWorker").value == "1",
                    state = LinkedProject.State.INITIALIZE
                )
            }
        }
    }

    private fun readBooleanAttribute(item: Element, name: String, default: Boolean): Boolean {
        val attribute = item.getAttribute(name)
        if (null === attribute) {
            return default
        }
        return attribute.value == "1"
    }

    private fun readStringAttribute(item: Element, name: String, default: String): String {
        val attribute = item.getAttribute(name)
        if (null === attribute) {
            return default
        }
        val value = attribute.value
        if (null === value || value.isEmpty()) {
            return default
        }
        return value
    }
}