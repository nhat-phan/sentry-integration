package net.ntworld.sentryIntegrationIdea.panel;

import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegrationIdea.Component
import javax.swing.*;

class ConnectionDetailPanel(): Component {
    var myWrapper: JPanel? = null
    var myUrl: JLabel? = null
    var myToken: JLabel? = null
    var myUser: JLabel? = null

    override val component: JComponent = myWrapper!!

    fun hide() {
        myWrapper!!.isVisible = false
    }

    fun show(connection: Connection) {
        myUrl!!.text = connection.url
        myToken!!.text = connection.getCensoredToken()
        if (null !== connection.user) {
            myUser!!.text = connection.user!!.username
        }
        myWrapper!!.isVisible = true
    }

}
