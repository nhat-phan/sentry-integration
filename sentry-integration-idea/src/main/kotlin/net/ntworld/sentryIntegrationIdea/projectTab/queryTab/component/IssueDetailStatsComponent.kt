package net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component

import net.ntworld.sentryIntegration.entity.SentryIssueStat
import net.ntworld.sentryIntegrationIdea.ComponentFactory
import net.ntworld.sentryIntegrationIdea.component.IssueStatComponent

class IssueDetailStatsComponent : AbstractDetailComponent("STATISTICS") {
    private val myTwentyFourHoursStatComponent = ComponentFactory.makeIssueStatComponent(IssueStatComponent.TwentyFourHoursStyle)
    private val myThirtyDaysStatComponent = ComponentFactory.makeIssueStatComponent(IssueStatComponent.ThirtyDaysStyle)

    init {
        container.add(myTwentyFourHoursStatComponent.component)
        container.add(myThirtyDaysStatComponent.component)
    }

    override fun onStateChange(state: State) {
        when (state) {
            State.LOADING -> {
            }
            State.SHOW -> {
                myTwentyFourHoursStatComponent.component.isVisible = true
                myThirtyDaysStatComponent.component.isVisible = true
                afterHandleShowState()
            }
            State.HIDE -> {
                myTwentyFourHoursStatComponent.component.isVisible = false
                myThirtyDaysStatComponent.component.isVisible = false
                afterHandleHideState()
            }
        }
    }

    fun setStats(twentyFourHoursStat: SentryIssueStat, thirtyDaysStat: SentryIssueStat) {
        myTwentyFourHoursStatComponent.setStat(twentyFourHoursStat)
        myThirtyDaysStatComponent.setStat(thirtyDaysStat)
        onStateChange(State.SHOW)
    }
}
