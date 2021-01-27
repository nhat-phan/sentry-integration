package net.ntworld.sentryIntegrationIdea.license

import net.ntworld.sentryIntegration.SentryApiManager
import net.ntworld.sentryIntegration.entity.LinkedProject

object LicenseChecker {
    private val grantedSitesLicense = listOf(
        Pair("https://sentry.io", "personio")
    )

    fun checkLicense(isPaidPlugin: Boolean, linkedProjects: List<LinkedProject>): Boolean {
        return checkSiteLicence(linkedProjects) || checkIntellijLicense(isPaidPlugin)
    }

    private fun checkSiteLicence(linkedProjects: List<LinkedProject>): Boolean {
        val cached = mutableMapOf<String, Boolean>()
        for (linkedProject in linkedProjects) {
            if (linkedProject.state != LinkedProject.State.READY) {
                continue
            }

            val hasSiteLicense = isSiteLicenseGranted(linkedProject)
            val isUsingFreeTier = cached[linkedProject.sentryOrganizationSlug]
            if (null === isUsingFreeTier) {
                val api = SentryApiManager.make(linkedProject)
                cached[linkedProject.sentryOrganizationSlug] = api.isUsingFreeTier()

                if (!hasSiteLicense && !cached[linkedProject.sentryOrganizationSlug]!!) {
                    return false
                }
                continue
            }

            if (!hasSiteLicense && !isUsingFreeTier) {
                return false
            }
        }
        return true
    }

    private fun isSiteLicenseGranted(linkedProject: LinkedProject): Boolean
    {
        for (granted in grantedSitesLicense) {
            if (linkedProject.connectionUrl == granted.first && linkedProject.sentryOrganizationSlug == granted.second) {
                return true
            }
        }
        return false
    }

    private fun checkIntellijLicense(isPaidPlugin: Boolean): Boolean
    {
        if (isPaidPlugin) {
            return IntellijCheckLicense.isLicensed
        }
        return false
    }
}