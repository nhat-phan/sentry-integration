package net.ntworld.sentryIntegrationIdea.projectTab

import com.intellij.openapi.Disposable
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.SimplePresenter

interface ProjectTabPresenter: SimplePresenter, Component, Disposable {
    val linkedProject: LinkedProject
}