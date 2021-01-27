package net.ntworld.sentryIntegrationIdea.notifier

import com.intellij.util.messages.Topic
import net.ntworld.sentryIntegration.entity.Connection

interface ConnectionNotifier {

    companion object {
        val TOPIC = Topic.create("SI:ConnectionNotifier", ConnectionNotifier::class.java)
    }

    fun connectionsChanged(connections: List<Connection>)

}