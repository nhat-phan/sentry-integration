package net.ntworld.sentryIntegrationIdea.projectTab.queryTab.component

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.EventDispatcher
import com.jetbrains.rd.util.first
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryIssue
import net.ntworld.sentryIntegrationIdea.node.Node
import net.ntworld.sentryIntegrationIdea.node.NodeFactory
import net.ntworld.sentryIntegrationIdea.node.SyncedTree
import net.ntworld.sentryIntegrationIdea.node.issue.CulpritNode
import net.ntworld.sentryIntegrationIdea.node.issue.ExceptionNode
import net.ntworld.sentryIntegrationIdea.node.issue.IssueNode
import net.ntworld.sentryIntegrationIdea.node.issue.IssuesTreeData
import net.ntworld.sentryIntegrationIdea.node.issue.SeenTimeNode
import net.ntworld.sentryIntegrationIdea.node.issue.StacktraceFrameNode
import net.ntworld.sentryIntegrationIdea.node.issue.TagsNode
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import javax.swing.JComponent
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.DefaultTreeSelectionModel
import javax.swing.tree.TreeCellRenderer
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

class IssuesTreeComponentImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject
): IssuesTreeComponent {
    private val dispatcher = EventDispatcher.create(IssuesTreeComponent.Listener::class.java)

    private val myTree = Tree()
    private val myRoot = DefaultMutableTreeNode()
    private val myModel = DefaultTreeModel(myRoot)
    private val myRenderer = NodeRenderer()
    private var myIsTreeRendering = false
    private val mySyncedTree: SyncedTree by lazy {
        NodeFactory.makeSyncedTree(myTree, myModel, myRoot)
    }
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
            handleOnTreeNodeSelectedEvent(myTree.selectionPaths)
        }
    }
    private val myTreeExpansionListener = object : TreeExpansionListener {
        override fun treeExpanded(event: TreeExpansionEvent?) {
            if (null !== event && !myIsTreeRendering) {
                handleOnTreeExpansionListener(event.path, true)
            }
        }

        override fun treeCollapsed(event: TreeExpansionEvent?) {
            if (null !== event && !myIsTreeRendering) {
                handleOnTreeExpansionListener(event.path, false)
            }
        }
    }

    override val component: JComponent = myTree

    init {
        val treeSelectionModel = DefaultTreeSelectionModel()
        treeSelectionModel.selectionMode = TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION

        myTree.model = myModel
        myTree.cellRenderer = myTreeCellRenderer
        myTree.isRootVisible = false
        myTree.selectionModel = treeSelectionModel

        myTree.addTreeSelectionListener(myTreeSelectionListener)
        myTree.addTreeExpansionListener(myTreeExpansionListener)
    }


    override fun addActionListener(listener: IssuesTreeComponent.Listener) {
        dispatcher.addListener(listener)
    }

    override fun removeActionListener(listener: IssuesTreeComponent.Listener) {
        dispatcher.removeListener(listener)
    }

    override fun setData(data: IssuesTreeData) {
        myIsTreeRendering = true
        val builder = NodeFactory.makeIssuesTreeRootNodeBuilder(data, projectServiceProvider.pluginConfiguration)
        val root = builder.build()
        NodeFactory.makeNodeSyncManager(projectServiceProvider, linkedProject).sync(root, mySyncedTree)
        // handleOnTreeNodeSelectedEvent(myTree.selectionPaths)

        myIsTreeRendering = false
    }

    override fun isIssueNodeExpanded(issue: SentryIssue): Boolean {
        val node = findTreeNodeContainsIssueNodeWithId(issue.id)
        if (null === node) {
            return false
        }
        return mySyncedTree.isExpand(node)
    }

    private fun findTreeNodeContainsIssueNodeWithId(issueId: String): DefaultMutableTreeNode? {
        for (child in myRoot.children()) {
            val item = child as? DefaultMutableTreeNode ?: continue
            val descriptor = item.userObject as? PresentableNodeDescriptor<*> ?: continue
            val node = descriptor.element as? Node ?: continue

            if (node is IssueNode && node.sentryIssue.id == issueId) {
                return item
            }
        }
        return null
    }

    private fun handleOnTreeNodeSelectedEvent(selectedPaths: Array<TreePath>?) {
        if (null === selectedPaths) {
            return
        }

        val sentryIssueMap = mutableMapOf<String, SentryIssue>()
        val stacktraceFrameMap = mutableMapOf<String, StacktraceFrameNode>()
        for (selectedPath in selectedPaths) {
            val lastPath = selectedPath.lastPathComponent as? DefaultMutableTreeNode ?: continue
            val descriptor = lastPath.userObject as? PresentableNodeDescriptor<*> ?: continue
            val node = descriptor.element as? Node ?: continue

            if (node is IssueNode) {
                sentryIssueMap[node.sentryIssue.id] = node.sentryIssue
                continue
            }

            if (node is CulpritNode) {
                sentryIssueMap[node.sentryIssue.id] = node.sentryIssue
                continue
            }

            if (node is SeenTimeNode) {
                sentryIssueMap[node.sentryIssue.id] = node.sentryIssue
                continue
            }

            if (node is TagsNode) {
                sentryIssueMap[node.sentryIssue.id] = node.sentryIssue
                continue
            }

            if (node is ExceptionNode) {
                sentryIssueMap[node.sentryIssue.id] = node.sentryIssue
                continue
            }

            if (node is StacktraceFrameNode) {
                sentryIssueMap[node.issue.id] = node.issue
                stacktraceFrameMap[node.issue.id] = node
                continue
            }
        }

        if (sentryIssueMap.isNotEmpty()) {
            dispatcher.multicaster.onIssuesSelected(sentryIssueMap.values.toList())

            if (stacktraceFrameMap.isNotEmpty() && stacktraceFrameMap.count() == 1) {
                val node = stacktraceFrameMap.first().value
                dispatcher.multicaster.onStacktraceSelected(
                    issue = node.issue,
                    exception =node.exception,
                    stacktrace = node.stacktrace,
                    index = node.index
                )
            }
        }
    }

    private fun handleOnTreeExpansionListener(selectedPath: TreePath?, isExpand: Boolean)
    {
        if (null == selectedPath) {
            return
        }
        val lastPath = selectedPath.lastPathComponent as? DefaultMutableTreeNode ?: return
        val descriptor = lastPath.userObject as? PresentableNodeDescriptor<*> ?: return
        val node = descriptor.element as? Node ?: return

        if (node is IssueNode) {
            if (isExpand) {
                dispatcher.multicaster.onIssueNodeExpanded(node.sentryIssue)
            } else {
                dispatcher.multicaster.onIssueNodeCollapsed(node.sentryIssue)
            }
        }
    }
}