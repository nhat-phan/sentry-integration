package net.ntworld.sentryIntegrationIdea.serviceProvider

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.debug
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.Scope
import net.ntworld.sentryIntegration.entity.SentryUserInfo
import net.ntworld.sentryIntegrationIdea.notifier.ConnectionNotifier
import org.jdom.Element

@State(name = "SentryIntegrationApplicationLevel", storages = [(Storage("sentry-integration.xml"))])
open class ApplicationServiceProviderImpl : ApplicationServiceProvider, PersistentStateComponent<Element> {
    private val myConnections = mutableMapOf<String, Connection>()
    private val myConnectionGetCurrentUserPairs = mutableMapOf<String, Pair<SentryUserInfo, Scope>>()

    override val isPaidPlugin: Boolean = true

    override val paidPluginUrl: String = "https://plugins.jetbrains.com/plugin/15945-sentry-integration"

    override val toolWindowConfigurationGroup: String = "sentry.integration.toolWindow"

    private val myProjectManagerListener = object: ProjectManagerListener {
        override fun projectOpened(project: Project) {
            // TODO: Start watcher
            if (connections.isEmpty()) {
                notifySetupConnection(project)
            }
        }
    }

    override val connections: List<Connection>
        get() = myConnections.values.toList()

    init {
        val messageBusConnection = ApplicationManager.getApplication().messageBus.connect()
        messageBusConnection.subscribe(ProjectManager.TOPIC, myProjectManagerListener)
    }

    override fun deleteConnection(project: Project, connection: Connection) {
        myConnections.remove(connection.id)
        myConnectionGetCurrentUserPairs.remove(connection.id)
        project.messageBus.syncPublisher(ConnectionNotifier.TOPIC).connectionsChanged(connections)
    }

    override fun saveConnection(project: Project, connection: Connection) {
        if (null === connection.user) {
            return
        }
        val currentConnection = myConnections[connection.id]
        if (null !== currentConnection && currentConnection.url == connection.url && currentConnection.token == connection.token) {
            return
        }
        myConnections[connection.id] = connection
        myConnectionGetCurrentUserPairs[connection.id] = Pair(connection.user!!, connection.scope)
        project.messageBus.syncPublisher(ConnectionNotifier.TOPIC).connectionsChanged(connections)
    }

    override fun getState(): Element? {
        debug("APPLICATION: getState() for saving persistence data")
        val element = Element("Connection")
        myConnections.values.forEach {
            val item = Element("Item")
            item.setAttribute("id", it.id)
            item.setAttribute("url", it.url)
            storeToken(it.id, it.token)
            element.addContent(item)
        }
        return element
    }

    override fun loadState(state: Element) {
        debug("APPLICATION: load persistence data from settings")
        for (item in state.children) {
            if (item.name != "Item") {
                continue
            }

            val id = item.getAttribute("id").value
            val url = item.getAttribute("url").value
            val token = receiveToken(id)
            if (null !== token) {
                if (myConnectionGetCurrentUserPairs.containsKey(id)) {
                    myConnections[id] = Connection(id, url, token, user = myConnectionGetCurrentUserPairs[id]!!.first, scope = myConnectionGetCurrentUserPairs[id]!!.second)
                } else {
                    val pair = SentryApiManager.make(url, token).getCurrentUser()
                    myConnections[id] = Connection(id, url, token, user = pair.first, scope = pair.second)
                    myConnectionGetCurrentUserPairs[id] = pair
                }
            }
        }
    }

    private fun notifySetupConnection(project: Project) {
        val notificationGroupManager = ApplicationManager.getApplication().getService(NotificationGroupManager::class.java)
        val notificationGroup = notificationGroupManager.getNotificationGroup(toolWindowConfigurationGroup)
        val notification = notificationGroup.createNotification(
            "Thank you for using the plugin, please click here to start",
            NotificationType.INFORMATION
        )
        notification.notify(project)
    }

    private fun storeToken(id: String, token: String) {
        PasswordSafe.instance.setPassword(
            CredentialAttributes("PSENTRYINTEG:$id", id),
            token
        )
    }

    private fun receiveToken(id: String): String? {
        return PasswordSafe.instance.getPassword(CredentialAttributes("PSENTRYINTEG:$id", id))
    }
}