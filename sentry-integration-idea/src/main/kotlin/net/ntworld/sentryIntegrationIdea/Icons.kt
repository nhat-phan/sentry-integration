package net.ntworld.sentryIntegrationIdea

import com.intellij.openapi.util.IconLoader

object Icons {
    val Trash = IconLoader.getIcon("/icons/trash.svg", this::class.java)
    val Ignore = IconLoader.getIcon("/icons/ban.svg", this::class.java)
    val Resolve = IconLoader.getIcon("/icons/check-gray.svg", this::class.java)
    val Resolved = IconLoader.getIcon("/icons/check-green.svg", this::class.java)
    val ExternalLink = IconLoader.getIcon("/icons/external-link-alt.svg", this::class.java)
    val Bookmark = IconLoader.getIcon("/icons/star-empty.svg", this::class.java)
    val Bookmarked = IconLoader.getIcon("/icons/star-filled.svg", this::class.java)
    val BookmarkedTreeIcon = IconLoader.getIcon("/icons/star-yellow.svg", this::class.java)
    val Seen = IconLoader.getIcon("/icons/eye.svg", this::class.java)
    val Unseen = IconLoader.getIcon("/icons/eye-slash.svg", this::class.java)
    val Subscribe = IconLoader.getIcon("/icons/bell.svg", this::class.java)
    val Unsubscribe = IconLoader.getIcon("/icons/bell-slash.svg", this::class.java)
    val EventNumberInTree = IconLoader.getIcon("/icons/list-ol.svg", this::class.java)
    val ClearCache = IconLoader.getIcon("/icons/eraser.svg", this::class.java)
    val CopyLink = IconLoader.getIcon("/icons/link.svg", this::class.java)
    val SetupWizard = IconLoader.getIcon("/icons/magic.svg", this::class.java)

    object Gutter {
        val LastFrame = IconLoader.getIcon("/icons/gutter-last-frame.svg", this::class.java)
        val Frame = IconLoader.getIcon("/icons/gutter-frame.svg", this::class.java)
    }
}
