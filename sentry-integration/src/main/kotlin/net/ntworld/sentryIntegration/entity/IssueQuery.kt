package net.ntworld.sentryIntegration.entity

data class IssueQuery(
    val resolved: Boolean? = null,
    val assignedToMe: Boolean = false,
    val lastSeen: String? = null,
    val bookmarked: Boolean? = null,
    val isAssigned: Boolean? = null
) {

    fun toQueryString(): String {
        val query = mutableListOf<String>()

        if (null === resolved || resolved == false) {
            query.add("is:unresolved")
        }
        if (resolved == true) {
            query.add("is:resolved")
        }

        if (null !== isAssigned) {
            query.add(if (isAssigned) "is:assigned" else "is:unassigned")
        }

        if (assignedToMe) {
            query.add("assigned:me")
        }

        if (null !== lastSeen) {
            query.add("lastSeen:$lastSeen")
        }

        if (null !== bookmarked && bookmarked) {
            query.add("bookmarks:me")
        }

        return query.joinToString(" ")
    }
}