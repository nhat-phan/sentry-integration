package net.ntworld.sentryIntegrationIdea.component

import net.ntworld.sentryIntegrationIdea.Component

interface SetupConnectionComponent: Component {
    fun getUrl(): String

    fun getToken(): String
}