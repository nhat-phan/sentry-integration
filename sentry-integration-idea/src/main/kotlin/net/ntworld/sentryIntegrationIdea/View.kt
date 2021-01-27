package net.ntworld.sentryIntegrationIdea

import java.util.*

interface View<ActionListener : EventListener> {
    fun addActionListener(listener: ActionListener)

    fun removeActionListener(listener: ActionListener)

}