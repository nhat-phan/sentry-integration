package net.ntworld.sentryIntegration

import net.ntworld.sentryIntegration.cache.CacheManager
import net.ntworld.sentryIntegration.client.CacheSentryApi
import net.ntworld.sentryIntegration.client.HttpClient
import net.ntworld.sentryIntegration.client.SentryApiImpl
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject

object SentryApiManager {
    private val myCachedApi = mutableMapOf<String, SentryApi>()

    fun make(connection: Connection): SentryApi {
        return SentryApiImpl(
            HttpClient(connection),
            LinkedProject.Empty
        )
    }

    fun make(linkedProject: LinkedProject, cache: Boolean = true): SentryApi {
        if (!cache) {
            return SentryApiImpl(
                HttpClient(linkedProject.connection),
                linkedProject
            )
        }

        val cachedApi = myCachedApi[linkedProject.id]
        if (null !== cachedApi) {
            return cachedApi
        }

        val api = SentryApiImpl(
            HttpClient(linkedProject.connection),
            linkedProject
        )
        val createdApi = CacheSentryApi(api, CacheManager.makeRequestCache(linkedProject))

        myCachedApi[linkedProject.id] = createdApi
        return createdApi
    }

    fun make(url: String, token: String): SentryApi {
        return SentryApiImpl(
            HttpClient(Connection("", url, token)),
            LinkedProject.Empty
        )
    }

}