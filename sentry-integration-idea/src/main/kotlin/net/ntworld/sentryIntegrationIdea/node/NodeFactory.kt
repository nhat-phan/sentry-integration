package net.ntworld.sentryIntegrationIdea.node

import net.ntworld.sentryIntegration.entity.Connection
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.PluginConfiguration
import net.ntworld.sentryIntegration.entity.SentryEventDetail
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.node.issue.IssuesTreeData
import net.ntworld.sentryIntegrationIdea.node.issue.IssuesTreeRootNodeBuilder
import net.ntworld.sentryIntegrationIdea.node.linkedProject.ProjectsTreeRootNodeBuilder
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

object NodeFactory {

    fun makeNodeSyncManager(projectServiceProvider: ProjectServiceProvider, linkedProject: LinkedProject? = null): NodeSyncManager {
        return NodeSyncManagerImpl(makeNodeDescriptorService(projectServiceProvider, linkedProject))
    }

    fun makeNodeDescriptorService(
        projectServiceProvider: ProjectServiceProvider,
        linkedProject: LinkedProject?
    ): NodeDescriptorService {
        return NodeDescriptorServiceImpl(projectServiceProvider, linkedProject)
    }

    fun makeSyncedTree(tree: JTree, treeModel: DefaultTreeModel, treeRoot: DefaultMutableTreeNode): SyncedTree {
        return SyncedTreeImpl(tree, treeModel, treeRoot)
    }

    fun makeProjectsTreeRootNodeBuilder(
        projects: List<LinkedProject>,
        connections: List<Connection>
    ): RootNodeBuilder {
        return ProjectsTreeRootNodeBuilder(projects, connections)
    }

    fun makeIssuesTreeRootNodeBuilder(
        data: IssuesTreeData,
        linkedProject: LinkedProject,
        pluginConfiguration: PluginConfiguration
    ): RootNodeBuilder {
        return IssuesTreeRootNodeBuilder(data, linkedProject, pluginConfiguration)
    }

}