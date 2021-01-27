package net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component

import com.intellij.util.ui.UIUtil
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegration.entity.SentryEventTag
import net.ntworld.sentryIntegrationIdea.util.EventTagsUtil
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class EventDetailTagsComponent(
    private val pluginConfiguration: PluginConfiguration
) : AbstractDetailComponent("TAGS") {
    private val myTagsWrapper = JPanel(MigLayout("wrap, insets 0", "5[right]5[fill,grow]5", "5[center]5"))

    init {
        container.add(myTagsWrapper)
    }

    override fun onStateChange(state: State) {
        when (state) {
            State.LOADING -> {
                myTagsWrapper.isVisible = false
            }
            State.SHOW -> {
                myTagsWrapper.isVisible = true
                afterHandleShowState()
            }
            State.HIDE -> {
                myTagsWrapper.isVisible = false
                afterHandleHideState()
            }
        }
    }

    fun setTags(tags: List<SentryEventTag>) {
        myTagsWrapper.removeAll()
        val data = EventTagsUtil.sortAndHighlightTagsByConfiguration(pluginConfiguration, tags)
        for (tag in data) {
            val label = JLabel()
            label.text = tag.key

            val textField = JTextField()
            textField.text = tag.value
            textField.border = null
            textField.background = null
            textField.isEditable = false
            if (!tag.highlighted) {
                label.foreground = UIUtil.getInactiveTextColor()
                textField.foreground = UIUtil.getInactiveTextColor()
            }

            myTagsWrapper.add(label)
            myTagsWrapper.add(textField)
        }
        onStateChange(State.SHOW)
    }
}
