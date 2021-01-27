package net.ntworld.sentryIntegrationIdea.setupWizard

import net.ntworld.sentryIntegrationIdea.Model
import java.util.*

interface SetupWizardModel: Model<SetupWizardModel.DataListener> {

    interface DataListener : EventListener {
    }
}