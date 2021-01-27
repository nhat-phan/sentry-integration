package net.ntworld.sentryIntegrationIdea.setupWizard

import com.intellij.ide.util.TipUIUtil
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ScrollPaneFactory
import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.LocalRepository
import net.ntworld.sentryIntegration.entity.SentryProject
import net.ntworld.sentryIntegrationIdea.AbstractView
import net.ntworld.sentryIntegrationIdea.setupWizard.component.CreateConnectionComponent
import net.ntworld.sentryIntegrationIdea.setupWizard.component.SelectConnectionComponent
import net.ntworld.sentryIntegrationIdea.setupWizard.component.SelectEnvironmentComponent
import net.ntworld.sentryIntegrationIdea.setupWizard.component.SelectProjectComponent

class SetupWizardViewImpl(): AbstractView<SetupWizardView.ActionListener>(), SetupWizardView {
    override val dispatcher = EventDispatcher.create(SetupWizardView.ActionListener::class.java)
    override val component: OnePixelSplitter = OnePixelSplitter(false, 0.33f, 0.33f, 0.33f)
    private val myProjectWrapper = OnePixelSplitter(false, 0.5f, 0.5f, 0.5f)
    private var mySelectConnectionComponent: SelectConnectionComponent? = null
    private val mySelectProjectComponent = SelectProjectComponent(dispatcher)
    private val mySelectEnvironmentComponent = SelectEnvironmentComponent(dispatcher)

    init {
        myProjectWrapper.firstComponent = mySelectProjectComponent.component
        myProjectWrapper.secondComponent = mySelectEnvironmentComponent.component
    }

    override fun displayCreateConnectionForm(showCancelButton: Boolean) {
        val createConnectionComponent = CreateConnectionComponent(dispatcher, showCancelButton)

        val webView = TipUIUtil.createBrowser() as TipUIUtil.Browser
        webView.text = this::class.java.getResource("/help/setup-connection.html").readText()

        component.firstComponent = createConnectionComponent.component
        component.secondComponent = ScrollPaneFactory.createScrollPane(webView.component, true)
    }

    override fun displaySelectConnectionStep(connections: List<Connection>, selectedConnectionId: String?) {
        val selectConnectionComponent = SelectConnectionComponent(dispatcher, connections, selectedConnectionId)

        selectConnectionComponent.setState(isActive = true)
        mySelectProjectComponent.setState(isActive = false)
        mySelectEnvironmentComponent.setState(isActive = false)
        component.firstComponent = selectConnectionComponent.component
        component.secondComponent = myProjectWrapper

        mySelectConnectionComponent = selectConnectionComponent
    }

    override fun displaySelectProjectStep(repositories: List<LocalRepository>, linkedProjects: List<LinkedProject>) {
        val selectConnectionComponent = mySelectConnectionComponent
        if (null !== selectConnectionComponent) {
            selectConnectionComponent.setState(isActive = false)
        }

        mySelectProjectComponent.setState(isActive = true)
        mySelectProjectComponent.setRepositories(repositories, linkedProjects)
    }

    override fun displayLoadingStateInFillEnvironmentForm() {
        mySelectProjectComponent.setState(isActive = false)
        mySelectEnvironmentComponent.setState(isActive = false)
        mySelectEnvironmentComponent.setTitleText("STEP 3/3: LOADING...")
    }

    override fun displayFillEnvironmentInformationStep(projects: List<SentryProject>, linkedProjects: List<LinkedProject>) {
        mySelectProjectComponent.setState(isActive = false)
        mySelectEnvironmentComponent.setState(isActive = true)

        mySelectEnvironmentComponent.setTitleText("STEP 3/3: ENVIRONMENT INFORMATION")
        mySelectEnvironmentComponent.setProjects(projects, linkedProjects)
    }

    override fun dispose() {
    }
}