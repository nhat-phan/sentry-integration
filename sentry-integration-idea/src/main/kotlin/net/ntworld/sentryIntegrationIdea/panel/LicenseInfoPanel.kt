package net.ntworld.sentryIntegrationIdea.panel;

import com.intellij.ide.BrowserUtil
import net.ntworld.sentryIntegrationIdea.Component
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class LicenseInfoPanel(url: String): Component {
    var myWrapper: JPanel? = null
    var myBuyPaidEditionButton: JButton? = null

    override val component: JComponent = myWrapper!!

    init {
        myBuyPaidEditionButton!!.addActionListener {
            BrowserUtil.open(url)
        }
    }
}
