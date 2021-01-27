package net.ntworld.sentryIntegrationIdea.projectTab

import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.Model
import java.util.*

interface ProjectTabModel: Model<ProjectTabModel.DataListener> {

    val linkedProject: LinkedProject

    var selectedTabName: String

    var teams: List<SentryTeam>

    var users: List<SentryUser>

    interface DataListener : EventListener {

    }
}