package net.ntworld.sentryIntegration.entity

class Scope(private val sentryAuthScope: SentryAuthScope?) {
    private val cacheMap = mutableMapOf<Functionality, Boolean>()

    fun canMutateIssues(): Boolean = has(Functionality.MutateIssues)

    fun getHelp(functionality: Functionality): Help = when (functionality) {
        Functionality.MutateIssues -> Help("project:write", "https://docs.sentry.io/api/events/bulk-mutate-a-list-of-issues/")
    }

    private fun has(functionality: Functionality): Boolean {
        val cached = cacheMap[functionality]
        if (null !== cached) {
            return cached
        }

        val help = getHelp(functionality)
        val result = null !== sentryAuthScope && sentryAuthScope.scopes.contains(help.scopeName)
        cacheMap[functionality] = result

        return result
    }

    enum class Functionality {
        MutateIssues,
    }

    data class Help(
        val scopeName: String,
        val url: String
    )
}
