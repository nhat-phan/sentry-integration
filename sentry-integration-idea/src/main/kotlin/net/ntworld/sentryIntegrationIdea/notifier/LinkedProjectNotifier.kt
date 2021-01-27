package net.ntworld.sentryIntegrationIdea.notifier

import com.intellij.util.messages.Topic
import net.ntworld.sentryIntegration.entity.LinkedProject

interface LinkedProjectNotifier {
    companion object {
        val TOPIC = Topic.create("SI:LinkedProjectNotifier", LinkedProjectNotifier::class.java)
    }

    fun linkedProjectsChanged(linkedProjects: List<LinkedProject>)
}