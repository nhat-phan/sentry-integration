package net.ntworld.sentryIntegrationIdea.util

object TextChoiceUtil {

    fun environment(count: Int): String {
        if (count == 0) {
            return "environments"
        }
        return if (count < 2) "$count environment" else "$count environments"
    }

    fun events(count: Int?): String {
        if (null === count) {
            return "events"
        }
        return if (count < 2) "$count event" else "$count events"
    }

}