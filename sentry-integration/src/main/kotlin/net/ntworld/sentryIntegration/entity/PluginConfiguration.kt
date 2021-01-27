package net.ntworld.sentryIntegration.entity

data class PluginConfiguration(
    val cacheDirectory: String,
    val prioritizedTags: String,
    val displayCulpritNode: Boolean,
    val markIssueAsSeenAutomatically: Boolean,
    val displayErrorLevelIcon: Boolean,
    val showEventCountAtTheEndOfIssueNode: Boolean,
    val grayOutUnsubscribeIssue: Boolean,
    val showSourceCodeOnStacktraceNode: Boolean
) {
    companion object {
        val Default = PluginConfiguration(
            "~/.idea/sentryIntegrationCache/",
            prioritizedTags = "browser,client_os,runtime",
            displayCulpritNode = true,
            markIssueAsSeenAutomatically = true,
            displayErrorLevelIcon = false,
            showEventCountAtTheEndOfIssueNode = true,
            grayOutUnsubscribeIssue = true,
            showSourceCodeOnStacktraceNode = true
        )
    }
}