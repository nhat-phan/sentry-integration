package net.ntworld.sentryIntegrationIdea.panel

import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.Component
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import javax.swing.*

class ProjectDetailPanel(private val projectServiceProvider: ProjectServiceProvider) : Component {
    var myWrapper: JPanel? = null
    var myLocalRootPath: JLabel? = null
    var myProject: JLabel? = null
    var myConnection: JLabel? = null
    var myEnvironment: JLabel? = null
    var mySentryProject: JLabel? = null
    var mySentryRootPath: JLabel? = null
    var myConnectionLabel: JLabel? = null
    var myEnvironmentLabel: JLabel? = null
    var myDeployedBranchLabel: JLabel? = null
    var myDeployedBranch: JLabel? = null
    var mySentryProjectLabel: JLabel? = null
    var mySentryRootPathLabel: JLabel? = null

    override val component: JComponent = myWrapper!!

    fun hide() {
        myWrapper!!.isVisible = false
    }

    fun displayProjects(projects: List<LinkedProject>) {
        if (projects.isEmpty()) {
            return
        }
        if (projects.count() == 1) {
            return displayProject(projects.first())
        }
        val first = projects.first()
        myProject!!.text = first.name
        myLocalRootPath!!.text = first.localRootPath

        myConnection!!.isVisible = false
        myEnvironment!!.isVisible = false
        mySentryProject!!.isVisible = false
        myDeployedBranch!!.isVisible = false
        mySentryRootPath!!.isVisible = false
        myConnectionLabel!!.isVisible = false
        myEnvironmentLabel!!.isVisible = false
        mySentryProjectLabel!!.isVisible = false
        myDeployedBranchLabel!!.isVisible = false
        mySentryRootPathLabel!!.isVisible = false

        myWrapper!!.isVisible = true
    }

    fun displayProject(project: LinkedProject) {
        val connection = findConnection(project)
        myProject!!.text = project.name
        myLocalRootPath!!.text = project.localRootPath
        if (null !== connection) {
            val user = connection.user
            if (null !== user) {
                myConnection!!.text = user.name + " - " + project.connectionUrl
            } else {
                myConnection!!.text = project.connectionUrl
            }
        } else {
            myConnection!!.text = project.connectionUrl
        }
        myEnvironment!!.text = project.environmentName
//        myEnvironment!!.text = project.environmentName + if (project.enableWorker)
//            " - Issues will fetched and displayed in editor"
//        else
//            " - Issues will not be displayed in editor"
        mySentryProject!!.text = project.sentryOrganizationSlug + " / " + project.sentryProjectSlug
        myDeployedBranch!!.text = project.deployedBranch
        mySentryRootPath!!.text = project.sentryRootPath

        myConnection!!.isVisible = true
        myEnvironment!!.isVisible = true
        mySentryProject!!.isVisible = true
        myDeployedBranch!!.isVisible = true
        mySentryRootPath!!.isVisible = true
        myConnectionLabel!!.isVisible = true
        myEnvironmentLabel!!.isVisible = true
        mySentryProjectLabel!!.isVisible = true
        myDeployedBranchLabel!!.isVisible = true
        mySentryRootPathLabel!!.isVisible = true

        myWrapper!!.isVisible = true
    }

    private fun findConnection(linkedProject: LinkedProject): Connection? {
        for (connection in projectServiceProvider.connections) {
            if (connection.id == linkedProject.connectionId) {
                return connection
            }
        }
        return null
    }
}
