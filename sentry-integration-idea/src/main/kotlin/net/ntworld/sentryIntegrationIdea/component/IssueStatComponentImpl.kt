package net.ntworld.sentryIntegrationIdea.component

import com.intellij.util.ui.UIUtil
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.DateTimeUtil
import net.ntworld.sentryIntegration.entity.SentryIssueStat
import net.ntworld.sentryIntegrationIdea.util.TextChoiceUtil
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JLabel
import javax.swing.JPanel

class IssueStatComponentImpl(
    private val style: IssueStatComponent.Style
) : IssueStatComponent {
    private val myChart = JPanel(null)
    private val myCachedPanels = mutableListOf<MyPanel>()
    private val myLabel = JLabel()
    private var myTotalCount = 0
    private var myChartType = SentryIssueStat.Type.TWENTY_FOUR_HOURS
    private val myStatItems = mutableListOf<SentryIssueStat.Item>()
    private val myChartMouseListener = object: MouseListener {
        override fun mouseClicked(e: MouseEvent?) {
        }

        override fun mousePressed(e: MouseEvent?) {
        }

        override fun mouseReleased(e: MouseEvent?) {
        }

        override fun mouseEntered(e: MouseEvent?) {
        }

        override fun mouseExited(e: MouseEvent?) {
            myLabel.text = if (myChartType == SentryIssueStat.Type.TWENTY_FOUR_HOURS) {
                "Last 24 hours: " + TextChoiceUtil.events(myTotalCount)
            } else {
                "Last 30 days: " + TextChoiceUtil.events(myTotalCount)
            }
        }
    }
    private val myPanelMouseListener = object: MouseListener {
        override fun mouseClicked(e: MouseEvent?) {
        }

        override fun mousePressed(e: MouseEvent?) {
        }

        override fun mouseReleased(e: MouseEvent?) {
        }

        override fun mouseEntered(e: MouseEvent?) {
            if (null === e) {
                return
            }

            val (index, panel) = findPanelIndex(e.component)
            if (index > -1 && index < myStatItems.count()) {
                val stat = myStatItems[index]
                panel!!.scale.background = style.hoveredColor

                val start = DateTimeUtil.fromTimestamp(stat.start * 1000)
                val end = DateTimeUtil.fromTimestamp(stat.end * 1000)

                myLabel.text = if (myChartType == SentryIssueStat.Type.TWENTY_FOUR_HOURS) {
                    DateTimeUtil.formatDate(start, "yyyy-MM-dd HH:mm") + " - " +
                        DateTimeUtil.formatDate(end, "HH:mm") +
                        ": " + TextChoiceUtil.events(stat.count)
                } else {
                    DateTimeUtil.formatDate(start, "yyyy-MM-dd HH:mm") + " - " +
                        DateTimeUtil.formatDate(end, "yyyy-MM-dd HH:mm") +
                        ": " + TextChoiceUtil.events(stat.count)
                }
            }
        }

        override fun mouseExited(e: MouseEvent?) {
            if (null === e) {
                return
            }

            val (index, panel) = findPanelIndex(e.component)
            if (index > -1 && index < myStatItems.count()) {
                panel!!.scale.background = style.backgroundColor
            }
        }

        private fun findPanelIndex(component: Component): Pair<Int, MyPanel?> {
            for (index in 0..myCachedPanels.lastIndex) {
                if (myCachedPanels[index].wrapper === component) {
                    return Pair(index, myCachedPanels[index])
                }
            }
            return Pair(-1, null)
        }
    }
    override val component = JPanel(MigLayout("fill", "0[center]0", "5[center]5"))

    init {
        myChart.background = UIUtil.getEditorPaneBackground()
        component.background = UIUtil.getEditorPaneBackground()

        myChart.addMouseListener(myChartMouseListener)
        component.addMouseListener(myChartMouseListener)
        component.add(myChart, "wrap")
        component.add(myLabel, "wrap")
    }

    override fun setStat(stat: SentryIssueStat) {
        hideAllPanels()
        myChartType = stat.type
        myStatItems.clear()
        myStatItems.addAll(stat.items)

        val max = findMaxValue(stat)
        for (i in 0..stat.items.lastIndex) {
            val item = stat.items[i]

            val panel = findPanel(i)

            val scale = findScaleHeight(item.count, max)
            val x = (style.itemWidth + style.itemWidthPadding) * i
            val y = (style.maxScaleHeight + style.itemHeightPadding) - scale - style.minScaleHeight
            val width = style.itemWidth
            val height = scale + style.minScaleHeight

            panel.setBounds(style.maxScaleHeight + style.itemHeightPadding, x, y, width, height)
        }
        myChart.preferredSize = Dimension(
            (style.itemWidth + style.itemWidthPadding) * stat.items.count(),
            style.maxScaleHeight + style.itemHeightPadding
        )

        myLabel.text = if (myChartType == SentryIssueStat.Type.TWENTY_FOUR_HOURS) {
            "Last 24 hours: " + TextChoiceUtil.events(myTotalCount)
        } else {
            "Last 30 days: " + TextChoiceUtil.events(myTotalCount)
        }
    }

    override fun showLoadingState() {
        hideAllPanels()
        myLabel.text = "Loading..."
    }

    private fun findMaxValue(stat: SentryIssueStat): Int {
        var total = 0
        var max = 0
        for (item in stat.items) {
            total += item.count
            if (item.count > max) {
                max = item.count
            }
        }
        myTotalCount = total
        return max
    }

    private fun findScaleHeight(count: Int, max: Int): Int {
        if (count == 0) {
            return 0
        }
        if (count == max) {
            return style.maxScaleHeight
        }

        val value = (count.toFloat() / max.toFloat()) * style.maxScaleHeight
        return value.toInt()
    }

    private fun hideAllPanels() {
        for (panel in myCachedPanels) {
            panel.wrapper.isVisible = false
        }
    }

    private fun findPanel(index: Int): MyPanel {
        if (index < myCachedPanels.count()) {
            val panel = myCachedPanels[index]
            panel.wrapper.isVisible = true
            return panel
        }

        val panel = MyPanel()
        panel.addMouseListener(myPanelMouseListener)

        panel.scale.background = style.backgroundColor
        myCachedPanels.add(panel)
        myChart.add(panel.wrapper)
        return panel
    }

    private class MyPanel() {
        val wrapper = JPanel(null)
        val scale = JPanel(null)

        init {
            wrapper.background = UIUtil.getEditorPaneBackground()
            wrapper.add(scale)
        }

        fun setBounds(maxHeight: Int, x: Int, y: Int, width: Int, height: Int) {
            wrapper.setBounds(x, 0, width, maxHeight)
            scale.setBounds(0, y, width, height)
        }

        fun addMouseListener(listener: MouseListener) {
            wrapper.addMouseListener(listener)
        }
    }
}