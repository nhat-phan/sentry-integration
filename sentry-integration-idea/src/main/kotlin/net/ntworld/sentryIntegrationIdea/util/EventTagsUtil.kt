package net.ntworld.sentryIntegrationIdea.util

import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegration.entity.SentryEventTag

object EventTagsUtil {
    private val myCachedMap = mutableMapOf<String, List<String>>()

    fun sortAndHighlightTagsByConfiguration(
        pluginConfiguration: PluginConfiguration,
        tags: List<SentryEventTag>
    ): List<TagData> {
        val config = getConfiguration(pluginConfiguration)
        return tags
            .map {
                if (config.contains(it.key)) {
                    TagData(
                        name = "0:${it.key}",
                        key = it.key,
                        value = it.value,
                        highlighted = true
                    )
                } else {
                    TagData(
                        name = "1:${it.key}",
                        key = it.key,
                        value = it.value,
                        highlighted = false
                    )
                }
            }
            .sortedBy { it.name }
    }

    fun getHighlightTags(
        pluginConfiguration: PluginConfiguration,
        tags: List<SentryEventTag>
    ): List<TagData> {
        return sortAndHighlightTagsByConfiguration(pluginConfiguration, tags).filter { it.highlighted }
    }

    private fun getConfiguration(pluginConfiguration: PluginConfiguration): List<String>
    {
        val cached = myCachedMap[pluginConfiguration.prioritizedTags]
        if (null !== cached) {
            return cached
        }

        val created = pluginConfiguration.prioritizedTags.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        myCachedMap[pluginConfiguration.prioritizedTags] = created
        return created
    }

    data class TagData(
        val name: String,
        val key: String,
        val value: String,
        val highlighted: Boolean
    )
}