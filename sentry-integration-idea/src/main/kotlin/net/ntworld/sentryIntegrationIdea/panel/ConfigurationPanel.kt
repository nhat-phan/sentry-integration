package net.ntworld.sentryIntegrationIdea.panel;

import com.intellij.ui.layout.selected
import net.ntworld.sentryIntegration.entity.PluginConfiguration
import javax.swing.*;

class ConfigurationPanel {
    var myWrapper: JPanel? = null
    var myTabbedPane: JTabbedPane? = null
    var myCacheDirectory: JTextField? = null
    var myPrioritizedTags: JTextField? = null
    var myMarkIssueAsSeenAutomatically: JCheckBox? = null
    var myDisplayErrorLevelIcon: JCheckBox? = null
    var myShowEventCountAtTheEndOfIssueNode: JCheckBox? = null
    var myGrayOutUnsubscribeIssue: JCheckBox? = null
    var myShowSourceCodeOnStacktraceNode: JCheckBox? = null
    var myDisplayCulpritNode: JCheckBox? = null

    fun setPluginConfiguration(pluginConfiguration: PluginConfiguration) {
        myCacheDirectory!!.text = pluginConfiguration.cacheDirectory
        myPrioritizedTags!!.text = pluginConfiguration.prioritizedTags
        myDisplayCulpritNode!!.isSelected = pluginConfiguration.displayCulpritNode
        myMarkIssueAsSeenAutomatically!!.isSelected = pluginConfiguration.markIssueAsSeenAutomatically
        myDisplayErrorLevelIcon!!.isSelected = pluginConfiguration.displayErrorLevelIcon
        myShowEventCountAtTheEndOfIssueNode!!.isSelected = pluginConfiguration.showEventCountAtTheEndOfIssueNode
        myGrayOutUnsubscribeIssue!!.isSelected = pluginConfiguration.grayOutUnsubscribeIssue
        myShowSourceCodeOnStacktraceNode!!.isSelected = pluginConfiguration.showSourceCodeOnStacktraceNode
    }

    fun getPluginConfiguration(): PluginConfiguration
    {
        return PluginConfiguration(
            myCacheDirectory!!.text.trim(),
            prioritizedTags = myPrioritizedTags!!.text.trim(),
            displayCulpritNode = myDisplayCulpritNode!!.isSelected,
            markIssueAsSeenAutomatically = myMarkIssueAsSeenAutomatically!!.isSelected,
            displayErrorLevelIcon = myDisplayErrorLevelIcon!!.isSelected,
            showEventCountAtTheEndOfIssueNode = myShowEventCountAtTheEndOfIssueNode!!.isSelected,
            grayOutUnsubscribeIssue = myGrayOutUnsubscribeIssue!!.isSelected,
            showSourceCodeOnStacktraceNode = myShowSourceCodeOnStacktraceNode!!.isSelected
        )
    }
}
