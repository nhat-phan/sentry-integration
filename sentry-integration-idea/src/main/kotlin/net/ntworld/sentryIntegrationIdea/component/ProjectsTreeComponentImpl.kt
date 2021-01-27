package net.ntworld.sentryIntegrationIdea.component

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.EventDispatcher
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.node.linkedProject.EnvironmentNode
import net.ntworld.sentryIntegrationIdea.node.Node
import net.ntworld.sentryIntegrationIdea.node.NodeDescriptorService
import net.ntworld.sentryIntegrationIdea.node.NodeFactory
import net.ntworld.sentryIntegrationIdea.node.linkedProject.ConnectionNode
import net.ntworld.sentryIntegrationIdea.node.linkedProject.ProjectNode
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import javax.swing.JComponent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.DefaultTreeSelectionModel
import javax.swing.tree.TreeCellRenderer
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel
import com.intellij.util.ui.tree.TreeUtil as IdeaTreeUtil

class ProjectsTreeComponentImpl(
    private val projectServiceProvider: ProjectServiceProvider
) : ProjectsTreeComponent {
    private val dispatcher = EventDispatcher.create(ProjectsTreeComponent.Listener::class.java)

    private val myTree = Tree()
    private val myRoot = DefaultMutableTreeNode()
    private val myModel = DefaultTreeModel(myRoot)
    private val myRenderer = NodeRenderer()
    private var myIsTreeRendering = false
    private val myTreeCellRenderer = TreeCellRenderer { tree, value, selected, expanded, leaf, row, hasFocus ->
        myRenderer.getTreeCellRendererComponent(
            tree,
            value,
            selected,
            expanded,
            leaf,
            row,
            hasFocus
        )
    }
    private val myTreeSelectionListener = TreeSelectionListener {
        if (null !== it && !myIsTreeRendering) {
            handleOnTreeNodeSelectedEvent(it.path)
        }
    }

    override val component: JComponent = myTree

    init {
        val treeSelectionModel = DefaultTreeSelectionModel()
        treeSelectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION

        myTree.model = myModel
        myTree.cellRenderer = myTreeCellRenderer
        myTree.isRootVisible = false
        myTree.selectionModel = treeSelectionModel

        myTree.addTreeSelectionListener(myTreeSelectionListener)
    }

    override fun addActionListener(listener: ProjectsTreeComponent.Listener) {
        dispatcher.addListener(listener)
    }

    override fun removeActionListener(listener: ProjectsTreeComponent.Listener) {
        dispatcher.removeListener(listener)
    }

    override fun setLinkedProjects(projects: List<LinkedProject>) {
        myIsTreeRendering = true
        val rootNode = NodeFactory.makeProjectsTreeRootNodeBuilder(projects, projectServiceProvider.connections).build()
        val newRoot = DefaultMutableTreeNode()
        buildTreeNode(newRoot, rootNode, NodeFactory.makeNodeDescriptorService(projectServiceProvider, null))
        myModel.setRoot(newRoot)
        myTree.selectionPath = IdeaTreeUtil.getPath(newRoot, newRoot)

        myIsTreeRendering = false
    }

    override fun expandAll() {
        IdeaTreeUtil.expandAll(myTree)
    }

    private fun handleOnTreeNodeSelectedEvent(selectedPath: TreePath?) {
        if (null === selectedPath) {
            return
        }

        val lastPath = selectedPath.lastPathComponent as? DefaultMutableTreeNode ?: return
        val descriptor = lastPath.userObject as? PresentableNodeDescriptor<*> ?: return
        val element = descriptor.element as? Node ?: return
        if (element is ConnectionNode) {
            dispatcher.multicaster.onProjectSelected(element.linkedProjects, true)
            return
        }
        if (element is ProjectNode) {
            dispatcher.multicaster.onProjectSelected(element.linkedProjects, false)
            return
        }
        if (element is EnvironmentNode) {
            dispatcher.multicaster.onEnvironmentSelected(element.linkedProject)
            return
        }
        dispatcher.multicaster.onProjectTreeUnselected()
    }

    private fun buildTreeNode(treeNode: DefaultMutableTreeNode, node: Node, nodeDescriptorService: NodeDescriptorService) {
        treeNode.userObject = nodeDescriptorService.make(node)
        for (item in node.children) {
            val child = DefaultMutableTreeNode()
            buildTreeNode(child, item, nodeDescriptorService)
            treeNode.add(child)
        }
    }
}