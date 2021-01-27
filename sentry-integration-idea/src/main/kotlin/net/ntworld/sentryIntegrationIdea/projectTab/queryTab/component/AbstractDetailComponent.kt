package net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component

import com.intellij.util.ui.JBUI
import net.miginfocom.swing.MigLayout
import net.ntworld.sentryIntegrationIdea.Component
import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.font.TextAttribute
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

abstract class AbstractDetailComponent(private val name: String, private val toggleVisibility: Boolean = true): Component {
    final override val component: JPanel = JPanel()
    protected val container = JPanel()
    private val myLabelWrapper = JPanel(MigLayout("wrap, insets 0", "0[]10[]", "center"))
    private val myLabel = JLabel()
    private val myShowLabel = JLabel()
    private val myMouseListener = object: MouseListener {
        override fun mouseClicked(e: MouseEvent?) {
            onStateChange(if (myShowLabel.text == "hide") State.HIDE else State.SHOW)
        }

        override fun mousePressed(e: MouseEvent?) {}
        override fun mouseReleased(e: MouseEvent?) {}
        override fun mouseEntered(e: MouseEvent?) {}
        override fun mouseExited(e: MouseEvent?) {}
    }

    init {
        component.layout = BoxLayout(component, BoxLayout.Y_AXIS)
        container.layout = BoxLayout(container, BoxLayout.Y_AXIS)

        val attributes: MutableMap<TextAttribute, Any?> = HashMap()
        attributes[TextAttribute.WEIGHT] = TextAttribute.WEIGHT_BOLD
        myLabel.text = name
        myLabel.font = Font.getFont(attributes)
        myLabel.foreground = Color.GRAY

        if (toggleVisibility) {
            myShowLabel.foreground = JBUI.CurrentTheme.Link.linkColor()
            myShowLabel.cursor = Cursor(Cursor.HAND_CURSOR)
        } else {
            myShowLabel.foreground = Color.GRAY
        }
        myShowLabel.addMouseListener(myMouseListener)

        myLabelWrapper.add(myLabel)
        myLabelWrapper.add(myShowLabel)

        component.add(myLabelWrapper)
        component.add(container)
    }

    fun showLoadingState() {
        myShowLabel.text = "loading..."
        myShowLabel.isVisible = true

        onStateChange(State.LOADING)
    }

    protected fun afterHandleShowState() {
        if (toggleVisibility) {
            myShowLabel.text = "hide"
            myShowLabel.isVisible = true
        } else {
            myShowLabel.isVisible = false
        }
    }

    protected fun afterHandleHideState() {
        if (toggleVisibility) {
            myShowLabel.text = "show"
            myShowLabel.isVisible = true
        } else {
            myShowLabel.isVisible = false
        }
    }

    protected abstract fun onStateChange(state: State)

    protected enum class State {
        LOADING,
        SHOW,
        HIDE
    }
}