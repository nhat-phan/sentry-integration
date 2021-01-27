package net.ntworld.sentryIntegration.cache

import net.ntworld.sentryIntegration.debug
import net.ntworld.sentryIntegration.entity.LinkedProject
import java.io.File
import java.math.BigInteger
import java.nio.file.Paths
import java.security.MessageDigest
import java.time.Clock

class FileCache(private val linkedProject: LinkedProject, private val group: String) : Cache {
    private val md5 = MessageDigest.getInstance("MD5")
    private val myTTLs = mutableMapOf<String, Long>()

    private fun findFilePath(key: String): String {
        return Paths.get(linkedProject.cacheBasePath, group, hashKey(key)).toString()
    }

    private fun findDirectoryPath(): String {
        return Paths.get(linkedProject.cacheBasePath, group).toString()
    }

    private fun hashKey(key: String): String {
        return BigInteger(1, md5.digest(key.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun getTime(): Long {
        return Clock.systemUTC().millis()
    }

    override fun has(key: String): Boolean {
        val file = File(findFilePath(key))

        return file.exists() && !file.isDirectory
    }

    override fun get(key: String): String? {
        val ttl = myTTLs[key]
        if (null === ttl) {
            debug("FileCache:get($key) no ttl")
            return null
        }

        val path = findFilePath(key)
        val file = File(path)

        if (file.exists() && !file.isDirectory) {
            val now = getTime()
            if (ttl > now) {
                val result = file.readText()
                debug("FileCache:get($key) found $path")
                return result
            }

            file.deleteOnExit()
            debug("FileCache:get($key) found but out of time, remove cache")
        }
        return null
    }

    override fun set(key: String, value: String, ttl: Int) {
        val directoryPath = findDirectoryPath()
        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val filePath = findFilePath(key)
        val file = File(filePath)

        debug("FileCache:set($key) with TTL $ttl to $filePath")
        file.writeText(value)
        myTTLs[key] = getTime() + (ttl * 1000)
    }

}