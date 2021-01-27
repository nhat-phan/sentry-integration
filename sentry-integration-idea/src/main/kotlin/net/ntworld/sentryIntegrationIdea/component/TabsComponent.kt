package net.ntworld.sentryIntegrationIdea.component

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.ui.tabs.JBTabs
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.TabsListener
import net.ntworld.sentryIntegrationIdea.Component
import javax.swing.JComponent

interface TabsComponent: Component {
    fun getTabs(): JBTabs

    fun setCommonCenterActionGroupFactory(factory: () -> ActionGroup)

    fun setCommonSideComponentFactory(factory: () -> JComponent)

    fun addTab(tabInfo: TabInfo)

    fun addListener(listener: TabsListener)

    override val component
        get() = getTabs().component
}