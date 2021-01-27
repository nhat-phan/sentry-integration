package net.ntworld.sentryIntegrationIdea.node

import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import javax.swing.tree.DefaultMutableTreeNode
import kotlin.math.min

class NodeSyncManagerImpl(
    private val nodeDescriptorService: NodeDescriptorService
): NodeSyncManager {

    override fun sync(root: RootNode, tree: SyncedTree) {
        val expandingNodes = mutableSetOf<String>()
        val selectedNodeToTopIds = mutableSetOf<String>()
        val selectedTreeNode = tree.selectedTreeNode()
        if (null !== selectedTreeNode) {
            val node = nodeDescriptorService.findNode(selectedTreeNode.userObject)
            var currentNode: Node? = node
            while (null !== currentNode) {
                selectedNodeToTopIds.add(currentNode.id)
                currentNode = currentNode.parent
            }
        }

        syncStructure(root, tree.treeRoot) { node, treeNode ->
            if (tree.isExpand(treeNode)) {
                expandingNodes.add(node.id)
            }
        }

        tree.nodeStructureChanged(tree.treeRoot)
        loopStructure(tree.treeRoot) { node, treeNode ->
            if (expandingNodes.contains(node.id)) {
                tree.expand(treeNode)
            }
            if (selectedNodeToTopIds.contains(node.id)) {
                tree.select(treeNode)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun loopStructure(treeNode: DefaultMutableTreeNode, visitor: (Node, DefaultMutableTreeNode) -> Unit) {
        visitor((treeNode.userObject as PresentableNodeDescriptor<Node>).element, treeNode)
        val children = treeNode.children();
        if (children != null) {
            while (children.hasMoreElements()) {
                val child = children.nextElement()
                loopStructure(child as DefaultMutableTreeNode, visitor)
            }
        }
    }

    private fun syncStructure(
        parent: Node,
        treeNode: DefaultMutableTreeNode,
        visitor: ((Node, DefaultMutableTreeNode) -> Unit)
    ) {
        visitor(parent, treeNode)
        treeNode.userObject = nodeDescriptorService.make(parent)

        val treeNodeChildren = treeNode.children().toList()
        val treeNodeChildCount = treeNodeChildren.size
        val index = min(treeNodeChildCount, parent.childCount)
        for (i in 0 until index) {
            val treeNodeChild = treeNodeChildren[i]
            syncStructure(parent.children[i], treeNodeChild as DefaultMutableTreeNode, visitor)
        }

        if (treeNodeChildCount < parent.childCount && index < parent.childCount) {
            for (i in index until parent.childCount) {
                val child = parent.children[i]
                val userObject = nodeDescriptorService.make(child)
                val childTreeNode = DefaultMutableTreeNode(userObject)
                syncStructure(child, childTreeNode, visitor)
                treeNode.add(childTreeNode)
            }
            return
        }

        if (treeNodeChildCount > parent.childCount && index < treeNode.childCount) {
            for (i in index until treeNodeChildCount) {
                // Always remove index because after removing 1 item the list is has 1 item less, then index
                // is the same :D
                treeNode.remove(index)
            }
            return
        }
    }

}