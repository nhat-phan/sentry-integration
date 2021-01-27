package net.ntworld.sentryIntegrationIdea.component

import net.ntworld.sentryIntegration.entity.SentryIssueStat
import net.ntworld.sentryIntegrationIdea.Component
import java.awt.Color

interface IssueStatComponent : Component {

    fun setStat(stat: SentryIssueStat)

    fun showLoadingState()

    data class Style(
        val minScaleHeight: Int,
        val maxScaleHeight: Int,
        val itemHeightPadding: Int,
        val itemWidthPadding: Int,
        val itemWidth: Int,
        val backgroundColor: Color,
        val hoveredColor: Color
    )

    companion object {
        // (style.itemWidth + style.itemWidthPadding) * stat.items.count()
        // (10 + 2) x 30 = (x + 2) x 24
        val TwentyFourHoursStyle = Style(
            minScaleHeight = 1,
            maxScaleHeight = 50,
            itemHeightPadding = 2,
            itemWidthPadding = 2,
            itemWidth = 13, // x = ((10 + 2) x 30) / 24 - 2
            backgroundColor = Color(199, 84, 80),
            hoveredColor = Color(247,194,81)
        )

        val ThirtyDaysStyle = Style(
            minScaleHeight = 1,
            maxScaleHeight = 50,
            itemHeightPadding = 2,
            itemWidthPadding = 2,
            itemWidth = 10,
            backgroundColor = Color(199, 84, 80),
            hoveredColor = Color(247,194,81)
        )
    }
}