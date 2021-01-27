package net.ntworld.sentryIntegrationIdea.toolWindow

import com.intellij.util.messages.Topic

interface MainToolWindowManager {
    companion object {
        val TOPIC = Topic.create("SI:MainToolWindowManager", MainToolWindowManager::class.java)
    }

    fun requestOpenProjectManager()

    fun requestOpenSetupWizard()

    fun requestCloseSetupWizard()
}