package net.ntworld.sentryIntegrationIdea.util

object UrlUtil {
    fun getBaseUrl(url: String): String {
        return if (url.endsWith('/')) {
            url.substring(0, url.length - 1)
        } else {
            url
        }
    }
}