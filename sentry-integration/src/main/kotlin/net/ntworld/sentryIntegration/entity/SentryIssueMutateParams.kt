package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

data class SentryIssueMutateParams(
    val status: String? = null,
    val isBookmarked: Boolean? = null,
    val isSubscribed: Boolean? = null,
    val ignoreDuration: Int? = null,
    val merge: Boolean? = null,
    val assignedTo: String? = null,
    val hasSeen: Boolean? = null
) {
    private val json = Json(JsonConfiguration.Stable.copy(strictMode = false))

    fun isValid(): Boolean {
        return null !== status ||
            null !== isBookmarked ||
            null !== isSubscribed ||
            null !== ignoreDuration ||
            null !== merge ||
            null !== assignedTo ||
            null !== hasSeen
    }

    fun toJson(): String {
        val map = mutableMapOf<String, JsonElement>()
        JsonElement
        if (null !== status) {
            map["status"] = JsonPrimitive(status)
        }
        if (null !== isBookmarked) {
            map["isBookmarked"] = JsonPrimitive(isBookmarked)
        }
        if (null !== isSubscribed) {
            map["isSubscribed"] = JsonPrimitive(isSubscribed)
        }
        if (null !== ignoreDuration) {
            map["ignoreDuration"] = JsonPrimitive(ignoreDuration)
        }
        if (null !== merge) {
            map["merge"] = JsonPrimitive(merge)
        }
        if (null !== assignedTo) {
            map["assignedTo"] = JsonPrimitive(assignedTo)
        }
        if (null !== hasSeen) {
            map["hasSeen"] = JsonPrimitive(hasSeen)
        }
        return json.stringify(JsonObject.serializer(), JsonObject(map))
    }
}