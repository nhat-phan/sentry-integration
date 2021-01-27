package net.ntworld.sentryIntegrationIdea.node

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

interface SyncedTree {
    val treeRoot: DefaultMutableTreeNode

    fun isExpand(treeNode: TreeNode): Boolean

    fun selectedTreeNode(): DefaultMutableTreeNode?

    fun expand(treeNode: TreeNode)

    fun select(treeNode: TreeNode)

    fun nodeStructureChanged(treeNode: TreeNode)
}