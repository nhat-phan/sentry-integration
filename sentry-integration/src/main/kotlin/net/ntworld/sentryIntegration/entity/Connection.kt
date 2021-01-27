package net.ntworld.sentryIntegration.entity

data class Connection(
    val id: String,
    val url: String,
    val token: String,
    val user: SentryUserInfo? = null,
    val scope: Scope = Scope(null)
) {
    fun getCensoredToken(): String {
        if (token.isEmpty()) {
            return ""
        }
        if (token.length > 20) {
            return token.substring(0, 8) + "..." + token.substring(token.lastIndex - 8)
        }
        return token
    }

    fun getStatus(): Status {
        return if (scope.canMutateIssues()) Status.ALL_GOOD else Status.WARNING
    }

    enum class Status {
        WARNING,
        ALL_GOOD
    }
}