package net.ntworld.sentryIntegrationIdea.serviceProvider

import com.intellij.openapi.project.Project
import net.ntworld.sentryIntegration.entity.Connection

interface ApplicationServiceProvider {

    val isPaidPlugin: Boolean

    val paidPluginUrl: String

    val toolWindowConfigurationGroup: String

    val connections: List<Connection>

    fun deleteConnection(project: Project, connection: Connection)

    fun saveConnection(project: Project, connection: Connection)

}