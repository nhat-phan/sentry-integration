package net.ntworld.sentryIntegration

import net.ntworld.sentryIntegration.entity.ErrorLevel

const val ENABLE_DEBUG_LOG = true

fun String.toCrossPlatformsPath(): String {
    return if (this.contains('\\')) this.replace('\\', '/') else this
}

internal fun makeErrorLevel(value: String) = when(value) {
    "info" -> ErrorLevel.Info
    "warning" -> ErrorLevel.Warning
    else -> ErrorLevel.Error
}

fun debug(text: String) {
    if (ENABLE_DEBUG_LOG) {
        println(text)
    }
}