package net.ntworld.sentryIntegrationIdea.util

import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject

object ModelUtil {
    fun isAnyConnectionChangedInMap(connections: List<Connection>, map: MutableMap<String, Connection>): Boolean {
        if (connections.count() != map.count()) {
            return true
        }

        for (connection in connections) {
            if (!map.containsKey(connection.id)) {
                return true
            }

            val item = map[connection.id]!!
            if (item.url != connection.url || item.token != connection.token) {
                return true
            }
        }
        return false
    }

    fun copyConnectionsToMap(connections: List<Connection>, map: MutableMap<String, Connection>) {
        map.clear()
        for (connection in connections) {
            map[connection.id] = connection
        }
    }

    fun isAnyProjectChangedInMap(projects: List<LinkedProject>, map: MutableMap<String, LinkedProject>): Boolean {
        if (projects.count() != map.count()) {
            return true
        }

        for (project in projects) {
            if (!map.containsKey(project.id)) {
                return true
            }

            val item = map[project.id]!!
            if (item != project) {
                return true
            }
        }
        return false
    }

    fun copyProjectsToMap(projects: List<LinkedProject>, map: MutableMap<String, LinkedProject>) {
        map.clear()
        for (project in projects) {
            map[project.id] = project
        }
    }
}