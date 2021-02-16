package net.ntworld.sentryIntegrationIdea.util

import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryProject

object LinkedProjectUtil {

    fun validateBeforeCreating(linkedProjects: List<LinkedProject>, data: CreateFormData): Pair<Boolean, String> {
        val missingFieldPair = checkAddProjectFormDataRequiredData(data)
        if (!missingFieldPair.first) {
            return missingFieldPair
        }

        for (project in linkedProjects) {
            if (project.name != data.name) {
                continue
            }

            if (project.localRootPath != data.localRootPath) {
                return Pair(false, "The project \"${data.name}\" already exists, please choose another name.")
            }

            if (project.environmentName == data.environmentName) {
                return Pair(false, "The environment \"${data.environmentName}\" of project \"${data.name}\" already exists, please choose another name.")
            }
        }
        return Pair(true, "")
    }

    fun validateBeforeUpdate(current: LinkedProject, linkedProjects: List<LinkedProject>, data: UpdateFormData): Pair<Boolean, String> {
        val missingFieldPair = checkEditProjectFormDataRequiredData(data)
        if (!missingFieldPair.first) {
            return missingFieldPair
        }

        for (project in linkedProjects) {
            if (project.id == current.id) {
                continue
            }

            if (project.name != data.name) {
                continue
            }

            if (project.environmentName == data.environmentName) {
                return Pair(false, "The environment \"${data.environmentName}\" of project \"${data.name}\" already exists, please choose another name.")
            }
        }
        return Pair(true, "")
    }

    private fun checkEditProjectFormDataRequiredData(data: UpdateFormData): Pair<Boolean, String> {
        val missingFields = mutableListOf<String>()

        if (data.name.isEmpty()) {
            missingFields.add("project's name")
        }

        if (data.environmentName.isEmpty()) {
            missingFields.add("environment's name")
        }

        if (missingFields.isEmpty()) {
            return Pair(true, "")
        }
        return Pair(false, "Please fill ${missingFields.joinToString(", ")}")
    }

    private fun checkAddProjectFormDataRequiredData(data: CreateFormData): Pair<Boolean, String> {
        val missingFields = mutableListOf<String>()

        if (data.name.isEmpty()) {
            missingFields.add("project's name")
        }

        if (data.environmentName.isEmpty()) {
            missingFields.add("environment's name")
        }

        if (data.localRootPath.isEmpty()) {
            missingFields.add("Local repository")
        }

        if (missingFields.isEmpty()) {
            return Pair(true, "")
        }
        return Pair(false, "Please fill ${missingFields.joinToString(", ")}")
    }

    data class CreateFormData(
        val connection: Connection,
        val name: String,
        val localRootPath: String,
        val sentryProject: SentryProject,
        val environmentName: String,
        val environmentRootPath: String,
        val useCompiledLanguage: Boolean,
        val deployedBranch: String
    )

    data class UpdateFormData(
        val id: String,
        val name: String,
        val sentryProject: SentryProject,
        val environmentName: String,
        val environmentRootPath: String,
        val useCompiledLanguage: Boolean,
        val deployedBranch: String
    )
}