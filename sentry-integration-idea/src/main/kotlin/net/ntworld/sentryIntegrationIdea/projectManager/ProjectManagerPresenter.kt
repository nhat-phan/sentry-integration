package net.ntworld.sentryIntegrationIdea.projectManager

import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.SimplePresenter

interface ProjectManagerPresenter: SimplePresenter, Component {
    val view: ProjectManagerView

    val model: ProjectManagerModel
}