package net.ntworld.sentryIntegration.cache

interface Cache {
    fun has(key: String): Boolean

    fun get(key: String): String?

    fun set(key: String, value: String, ttl: Int)
}