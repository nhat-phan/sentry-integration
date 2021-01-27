package net.ntworld.sentryIntegrationIdea.node.linkedProject

import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.node.RootNode
import net.ntworld.sentryIntegrationIdea.node.RootNodeBuilder

class ProjectsTreeRootNodeBuilder(
    private val projects: List<LinkedProject>,
    private val connections: List<Connection>
): RootNodeBuilder {
    override fun build(): RootNode {
        val root = RootNode()
        val connectionNodeMap = mutableMapOf<String, ConnectionNode>()
        for (connection in connections) {
            val user = connection.user
            if (null === user) {
                continue
            }

            connectionNodeMap[connection.id] = ConnectionNode(
                id = connection.id,
                name = connection.url + " - " + user.username,
                linkedProjects = projects.filter { it.connectionId == connection.id },
                isValid = true
            )
        }

        val map = mutableMapOf<String, MutableMap<String, MutableList<LinkedProject>>>()
        for (project in projects) {
            // Remap and build missing connection
            val connectionNode = connectionNodeMap[project.connectionId]
            if (null === connectionNode) {
                connectionNodeMap[project.connectionId] = ConnectionNode(
                    id = project.connectionId,
                    name = "Unknown",
                    linkedProjects = projects.filter { it.connectionId == project.connection.id },
                    isValid = false
                )
            }

            // Build project map
            val projectMap = map[project.connection.id]
            if (null === projectMap) {
                map[project.connection.id] = mutableMapOf()
            }

            if (!map[project.connection.id]!!.containsKey(project.name)) {
                map[project.connection.id]!![project.name] = mutableListOf()
            }
            map[project.connection.id]!![project.name]!!.add(project)
        }

        for (item in map) {
            val projectMap = item.value
            val connectionNode = connectionNodeMap[item.key]!!

            val keys = projectMap.keys.sorted()
            for (name in keys) {
                val projectNode = ProjectNode(name, projectMap[name]!!)
                val mutableList = projectMap[name]!!
                if (mutableList.count() > 1) {
                    mutableList.sortBy { it.environmentName }
                    for (linkedProject in mutableList) {
                        val envNode = EnvironmentNode(linkedProject)

                        projectNode.add(envNode)
                    }
                }
                connectionNode.add(projectNode)
            }
            root.add(connectionNode)
        }
        return root
    }
}