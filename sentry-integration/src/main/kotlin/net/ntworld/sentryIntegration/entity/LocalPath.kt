package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Serializable
import net.ntworld.sentryIntegration.toCrossPlatformsPath

@Serializable
data class LocalPath(
    val originValue: String,
    val value: String,
    val sentryRootPath: String
) {
    fun isDifferent(): Boolean {
        return value != originValue
    }

    companion object {
        fun create(input: String, sentryRootPath: String): LocalPath {
            if (sentryRootPath.isEmpty()) {
                return LocalPath(originValue = input, value = input, sentryRootPath = sentryRootPath)
            }

            val path = input.toCrossPlatformsPath()
            if (path.startsWith(sentryRootPath)) {
                return LocalPath(
                    originValue = input,
                    value = path.substring(sentryRootPath.length),
                    sentryRootPath = sentryRootPath
                )
            }

            return LocalPath(
                originValue = input,
                value = input,
                sentryRootPath = sentryRootPath
            )
        }
    }
}