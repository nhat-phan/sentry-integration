package net.ntworld.sentryIntegration.cache

import net.ntworld.sentryIntegration.entity.LinkedProject
import java.nio.file.Paths

object CacheManager {

    fun makeRequestCache(linkedProject: LinkedProject): Cache {
        return FileCache(linkedProject, "requests")
    }

    fun clear(linkedProject: LinkedProject) {
        val path = Paths.get(linkedProject.cacheBasePath)
        val file = path.toFile()
        if (file.isDirectory) {
            file.deleteRecursively()
        }
    }

}