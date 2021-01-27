package net.ntworld.sentryIntegrationIdea

import java.util.*

interface Model<DataListener : EventListener> {
    fun addDataListener(listener: DataListener)

    fun removeDataListener(listener: DataListener)
}