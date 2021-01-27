package net.ntworld.sentryIntegrationIdea.projectTab

import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryTeam
import net.ntworld.sentryIntegration.entity.SentryUser
import net.ntworld.sentryIntegrationIdea.AbstractModel

class ProjectTabModelImpl(
    override val linkedProject: LinkedProject
) : AbstractModel<ProjectTabModel.DataListener>(), ProjectTabModel {
    override val dispatcher = EventDispatcher.create(ProjectTabModel.DataListener::class.java)

    override var selectedTabName: String = ""

    override var teams: List<SentryTeam> = listOf()

    override var users: List<SentryUser> = listOf()
}