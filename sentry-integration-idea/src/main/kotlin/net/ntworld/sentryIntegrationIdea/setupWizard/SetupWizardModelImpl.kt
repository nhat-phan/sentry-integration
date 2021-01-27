package net.ntworld.sentryIntegrationIdea.setupWizard

import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegrationIdea.AbstractModel

class SetupWizardModelImpl(): AbstractModel<SetupWizardModel.DataListener>(), SetupWizardModel {
    override val dispatcher = EventDispatcher.create(SetupWizardModel.DataListener::class.java)

}