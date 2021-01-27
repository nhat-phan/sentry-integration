package net.ntworld.sentryIntegrationIdea.node

import com.intellij.util.ui.tree.TreeUtil
import java.lang.NullPointerException
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

class SyncedTreeImpl(
    private val tree: JTree,
    private val treeModel: DefaultTreeModel,
    override val treeRoot: DefaultMutableTreeNode
) : SyncedTree {
    private fun findPath(treeNode: TreeNode): TreePath? {
        return try {
            TreeUtil.getPath(treeRoot, treeNode)
        } catch (exception: NullPointerException) {
            null
        }
    }

    override fun isExpand(treeNode: TreeNode): Boolean {
        val path = findPath(treeNode)
        return if (null !== path) tree.isExpanded(path) else false
    }

    override fun selectedTreeNode(): DefaultMutableTreeNode? {
        return if (null === tree.selectionPath) {
            null
        } else {
            tree.selectionPath!!.lastPathComponent as DefaultMutableTreeNode?
        }
    }

    override fun select(treeNode: TreeNode) {
        val path = findPath(treeNode)
        if (null !== path) {
            tree.selectionPath = path
        }
    }

    override fun expand(treeNode: TreeNode) {
        val path = findPath(treeNode)
        if (null !== path) {
            tree.expandPath(path)
        }
    }

    override fun nodeStructureChanged(treeNode: TreeNode) {
        treeModel.nodeStructureChanged(treeNode)
    }
}