package net.ntworld.sentryIntegrationIdea.setupWizard.component

import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegrationIdea.Component
import java.awt.Color
import java.awt.Font
import java.awt.font.TextAttribute
import javax.swing.JLabel
import javax.swing.JPanel

abstract class AbstractWizardStep(
    private val text: String
): Component {
    final override val component: JPanel = JPanel(MigLayout("wrap, insets 0", "10[fill,grow]10", "10[fill,grow]"))
    private var myIsActive: Boolean = false
    private val myLabel = JLabel()

    val isActive: Boolean
        get() = myIsActive

    init {
        val myLabelWrapper = JPanel(MigLayout("wrap, insets 0", "push[]push", "10[center]0"))
        val attributes: MutableMap<TextAttribute, Any?> = HashMap()
        attributes[TextAttribute.WEIGHT] = TextAttribute.WEIGHT_BOLD
        myLabel.text = text
        myLabel.font = Font.getFont(attributes)
        myLabel.foreground = Color.GRAY
        myLabelWrapper.background = UIUtil.getEditorPaneBackground()
        myLabelWrapper.add(myLabel)

        component.add(myLabelWrapper, "dock north")
        component.background = UIUtil.getEditorPaneBackground()
    }

    fun setState(isActive: Boolean) {
        myLabel.foreground = if (isActive) JBUI.CurrentTheme.Link.linkColor() else Color.GRAY
        myIsActive = isActive
        onStateChanged()
    }

    fun setTitleText(value: String) {
        myLabel.text = value
    }

    protected abstract fun onStateChanged()

    protected fun makeLabel(text: String): JLabel {
        val label = JLabel()
        label.text = text
        return label
    }

    protected fun makeEmptyComponent() = JLabel()
}